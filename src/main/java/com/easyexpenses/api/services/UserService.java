package com.easyexpenses.api.services;

import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User mockUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");

        return userRepository.save(user);
    }

    public User getUser(Long id){
        return userRepository.findById(id).orElseThrow();
    }
}
