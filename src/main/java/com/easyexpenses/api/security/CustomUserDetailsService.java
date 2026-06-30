package com.easyexpenses.api.security;

import com.easyexpenses.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return new CustomUserDetails(userService.findByUsername(username));
    }

    public UserDetails loadUserById(String id) {
        return new CustomUserDetails(userService.getUser(Long.valueOf(id)));
    }
}