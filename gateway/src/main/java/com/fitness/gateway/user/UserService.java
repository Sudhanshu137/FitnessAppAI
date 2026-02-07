package com.fitness.gateway.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validate(String userid) {


            return userServiceWebClient.get()
                    .uri("/api/users/{userid}/validate", userid) // ahead of base url https://locahost:8082/
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorResume(WebClientResponseException.class,e->{

                       if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                           return Mono.error(new RuntimeException("User Not Found "+ userid));
                       } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                           return Mono.error(new RuntimeException("Invalid Request: "+ userid));
                       }
                       return Mono.error(new RuntimeException("Unexpected error" + e.getMessage()));
                            }
                    );


    }

    public Mono<UserResponse> registerUser(RegisterRequest registerRequest) {

        log.info("Calling User Registration API for email:{}",registerRequest.getEmail());
        return userServiceWebClient.post()
                .uri("/api/users/register") // ahead of base url https://locahost:8082/
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class,e->{

                            if(e.getStatusCode() == HttpStatus.BAD_REQUEST){
                                return Mono.error(new RuntimeException(".BAD REQUEST "+ e.getMessage()));
                            } else if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                                return Mono.error(new RuntimeException("Internal Server error: "+ e.getMessage()));
                            }
                            return Mono.error(new RuntimeException("Unexpected error" + e.getMessage()));

                        }
                );
    }
}
