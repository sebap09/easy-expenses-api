package com.easyexpenses.api.controllers;

import com.easyexpenses.api.dtos.userprofile.request.UserProfileRequest;
import com.easyexpenses.api.dtos.userprofile.response.UserProfileResponse;
import com.easyexpenses.api.services.UserProfileService;
import com.easyexpenses.api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/me")
public class UserProfileController {
    private final UserService userService;
    private final UserProfileService userProfileService;

    public UserProfileController(UserService userService, UserProfileService userProfileService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> me(@AuthenticationPrincipal Jwt jwt) {
        Long userId = userService.findOrCreateUser(jwt);
        return ResponseEntity.ok(userProfileService.getUserProfileData(userId));
    }

    @PostMapping
    public ResponseEntity<UserProfileResponse> saveConfiguration(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UserProfileRequest userProfileRequest) {
        Long userId = userService.findOrCreateUser(jwt);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userProfileService.setUserProfileData(userId, userProfileRequest));
    }
}
