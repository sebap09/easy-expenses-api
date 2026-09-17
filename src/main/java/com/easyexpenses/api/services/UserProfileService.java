package com.easyexpenses.api.services;

import com.easyexpenses.api.dtos.UserProfileResponse;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.errors.ResourceNotFoundException;
import com.easyexpenses.api.mappers.UserProfileMapper;
import com.easyexpenses.api.repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserService userService;
    private final UserProfileMapper userProfileMapper;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, UserService userService, UserProfileMapper userProfileMapper) {
        this.userProfileRepository = userProfileRepository;
        this.userService = userService;
        this.userProfileMapper = userProfileMapper;
    }

    public UserProfileResponse findOrCreateUser(Jwt jwt){
        return userProfileMapper.toResponse(userService.findOrCreateUser(jwt));
    }

    public UserProfile getUserProfile(Long id){
        return userProfileRepository.findByUserId(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + id));
    }
}
