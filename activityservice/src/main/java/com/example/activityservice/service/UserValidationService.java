package com.example.activityservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final WebClient userServiceWebClient;

    public boolean validate(String userid) {

        try {
            return userServiceWebClient.get()
                    .uri("/api/users/{userid}/validate", userid) // ahead of base url https://locahost:8082/
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (WebClientResponseException e) {
             if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                 throw new RuntimeException("User Not Found:" + userid);
             } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                 throw new RuntimeException("Invalid Request:"+userid);
             }
            throw new RuntimeException("User service error: " + e.getStatusCode());
        }

    }
}
