package com.easyexpenses.api.security;

import com.easyexpenses.api.builders.UserBuilder;
import com.easyexpenses.api.dtos.RegistrationRequest;
import com.easyexpenses.api.dtos.RegistrationResponse;
import com.easyexpenses.api.entities.Role;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest){
        User user = new UserBuilder()
                .username(registrationRequest.username())
                .password(passwordEncoder.encode(registrationRequest.rawPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userService.save(user);
        return new RegistrationResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                "User registered successfully"
        );
    }
}
