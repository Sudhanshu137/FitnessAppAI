package com.fitness.gateway;


import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
       // String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");// if userid exits means passed as header if ,extra code just(no needed)
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        RegisterRequest registerRequest = getUserDetails(token);

        if(registerRequest.getKeycloakId()!=null && token!=null){
            return userService.validate(registerRequest.getKeycloakId())
                    .flatMap(exist ->{
                        if(!exist){
                            //Register User
                            if(registerRequest!=null){
                                return userService.registerUser(registerRequest)
                                        .then(Mono.empty());
                            }else {
                                return Mono.empty();
                            }
                        }else{
                            log.info("User already exits, Skipping sync");
                            return Mono.empty();
                        }
                    })
                    .then(Mono.defer(()->{
                        ServerHttpRequest mutatedRequest = (ServerHttpRequest) exchange.getRequest().mutate()
                                .header("X-User-ID",registerRequest.getKeycloakId())
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));
        }
        return chain.filter(exchange);
    }

    private RegisterRequest getUserDetails(String token) {

        try{
            String tokenWithoutBearer = token.replace("Bearer ","").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(claims.getStringClaim("email"));
            registerRequest.setKeycloakId(claims.getStringClaim("sub"));
            registerRequest.setPassword("dummypassword");
            registerRequest.setFirstname(claims.getStringClaim("given_name"));
            registerRequest.setLastname(claims.getStringClaim("family_name"));
            return registerRequest;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
