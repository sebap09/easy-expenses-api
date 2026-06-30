package com.easyexpenses.api.security;

import com.easyexpenses.api.builders.UserBuilder;
import com.easyexpenses.api.dtos.AuthRequest;
import com.easyexpenses.api.dtos.AuthResponse;
import com.easyexpenses.api.dtos.RegistrationRequest;
import com.easyexpenses.api.dtos.RegistrationResponse;
import com.easyexpenses.api.entities.Role;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.services.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final Duration expiresIn = Duration.ofHours(1);

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest){
        User user = new UserBuilder()
                .username(registrationRequest.username())
                .password(passwordEncoder.encode(registrationRequest.rawPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userService.save(user);
        return new RegistrationResponse(savedUser.getId(), savedUser.getUsername(), "User registered successfully");
    }

    public AuthResponse authenticate(AuthRequest authRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.username(),
                        authRequest.password()
                )
        );
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        JwtGenerationRequest jwtGenerationRequest = new JwtGenerationRequest(Objects.requireNonNull(customUserDetails).getId().toString(), expiresIn, "MyIssuer", "MyAudience");
        //TODO: implement refreshToken
        return new AuthResponse(jwtService.generateToken(jwtGenerationRequest), "Bearer", expiresIn.getSeconds(), "refreshToken");
    }
}
