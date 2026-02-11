package com.fitness.userservice.service;

import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import com.fitness.userservice.service.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    public UserResponse register(@Valid RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            User existingUser = userRepository.findByEmail(request.getEmail());
            UserResponse userResponse = new UserResponse();
            userResponse.setEmail(existingUser.getEmail());
            userResponse.setKeycloakId(existingUser.getKeycloakId());
            userResponse.setPassword(existingUser.getPassword());
            userResponse.setId(existingUser.getId());
            userResponse.setFirstName(existingUser.getFirstName());
            userResponse.setLastName(existingUser.getLastName());
            userResponse.setCreatedAt(existingUser.getCreatedAt());
            userResponse.setUpdatedAt(existingUser.getUpdatedAt());
            return userResponse;
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setKeycloakId(request.getKeycloakId());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());

        User saveduser = userRepository.save(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(saveduser.getEmail());
        userResponse.setKeycloakId(user.getKeycloakId());
        userResponse.setPassword(saveduser.getPassword());
        userResponse.setId(saveduser.getId());
        userResponse.setFirstName(saveduser.getFirstName());
        userResponse.setLastName(saveduser.getLastName());
        userResponse.setCreatedAt(saveduser.getCreatedAt());
        userResponse.setUpdatedAt(saveduser.getUpdatedAt());
        return userResponse;
    }

    public UserResponse getUserProfiles(String userid) {
        User user = userRepository.findById(userid)
                   .orElseThrow(() -> new RuntimeException("No user exits by this id"));

        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setPassword(user.getPassword());
        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setKeycloakId(user.getKeycloakId());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        return userResponse;

    }

    public Boolean existByUserId(String userid) {
        return userRepository.existsById(userid);
    }
}
