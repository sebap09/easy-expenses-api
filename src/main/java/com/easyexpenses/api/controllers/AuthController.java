package com.easyexpenses.api.controllers;

import com.easyexpenses.api.dtos.AuthRequest;
import com.easyexpenses.api.dtos.RegistrationRequest;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    // POST http://localhost:8080/api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegistrationRequest registrationRequest) {
        return new ResponseEntity<>(userService.register(registrationRequest), HttpStatus.CREATED);
    }

    // POST http://localhost:8080/api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.username(),
                        authRequest.password()
                )
        );

        return ResponseEntity.ok(
                "Login successful"
        );
    }
}
