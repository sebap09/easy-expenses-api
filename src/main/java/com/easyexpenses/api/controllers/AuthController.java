package com.easyexpenses.api.controllers;

import com.easyexpenses.api.dtos.RegistrationRequest;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // POST http://localhost:8080/api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegistrationRequest registrationRequest) {
        return new ResponseEntity<>(userService.register(registrationRequest), HttpStatus.CREATED);
    }
}
