package com.fitness.userservice.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email format")
    private String email;

    @NotNull
    private String keycloakId;

    @NotBlank(message = "Password is Required")
    @Size(min = 6,message = "password must have atleast 6 characters")
    private String password;
    private String firstname;
    private String lastname;


}
