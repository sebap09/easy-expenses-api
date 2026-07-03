package com.easyexpenses.api.controllers;

import com.easyexpenses.api.dtos.*;
import com.easyexpenses.api.security.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST http://localhost:8080/api/v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return new ResponseEntity<>(authService.register(registrationRequest), HttpStatus.CREATED);
    }

    // POST http://localhost:8080/api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        return new ResponseEntity<>(authService.authenticate(authRequest), HttpStatus.OK);
    }

    // POST http://localhost:8080/api/v1/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
        return new ResponseEntity<>(authService.refresh(refreshRequest), HttpStatus.OK);
    }

    // POST http://localhost:8080/api/v1/auth/revoke
    @PostMapping("/revoke")
    public ResponseEntity<Void> revoke(@Valid @RequestBody RevokeRequest revokeRequest) {
        authService.revokeToken(revokeRequest);
        return ResponseEntity.noContent().build();
    }
}
