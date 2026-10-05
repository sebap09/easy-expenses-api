package com.easyexpenses.api.controllers;

import com.easyexpenses.api.dtos.userprofile.request.UserProfileRequest;
import com.easyexpenses.api.dtos.userprofile.response.UserProfileResponse;
import com.easyexpenses.api.entities.UserProfile;
import com.easyexpenses.api.services.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/me")
public class UserProfileController {
    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> me(@AuthenticationPrincipal Jwt jwt) {
        UserProfile userProfile = userProfileService.findOrCreateUser(jwt);
        return new ResponseEntity<>(userProfileService.getUserProfileData(userProfile), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserProfileResponse> saveConfiguration(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UserProfileRequest userProfileRequest) {
        UserProfile userProfile = userProfileService.findOrCreateUser(jwt);
        return new ResponseEntity<>(userProfileService.setUserProfileData(userProfile, userProfileRequest), HttpStatus.OK);
    }
}
