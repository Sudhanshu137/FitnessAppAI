package com.fitness.userservice.controller;


import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.service.UserService;
import com.fitness.userservice.service.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userid}")
    public ResponseEntity<UserResponse> getUserprofiles(@PathVariable String userid){
        return ResponseEntity.ok(userService.getUserProfiles(userid));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/{userid}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable String userid){
        return ResponseEntity.ok(userService.existByUserId(userid));
    }


}
