package com.easyexpenses.api.services;

import com.easyexpenses.api.builders.UserBuilder;
import com.easyexpenses.api.dtos.RegistrationRequest;
import com.easyexpenses.api.entities.Role;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUser(Long id){
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + id));
    }

    public User register(RegistrationRequest registrationRequest){
        User user = new UserBuilder()
                .username(registrationRequest.username())
                .password(passwordEncoder.encode(registrationRequest.rawPassword()))
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }
}
