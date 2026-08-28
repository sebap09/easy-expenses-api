package com.easyexpenses.api.controllers;

import com.easyexpenses.api.dtos.UserProfileResponse;
import com.easyexpenses.api.services.UserProfileService;
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

    // GET http://localhost:8080/api/v1/me
    @GetMapping
    public ResponseEntity<UserProfileResponse> me(@AuthenticationPrincipal Jwt jwt) {
        return new ResponseEntity<>(userProfileService.findOrCreateUser(jwt), HttpStatus.OK);
    }

}
