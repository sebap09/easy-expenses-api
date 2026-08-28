package com.easyexpenses.api.mappers;

import com.easyexpenses.api.dtos.UserProfileResponse;
import com.easyexpenses.api.entities.UserProfile;
import org.springframework.stereotype.Service;

@Service
public class UserProfileMapper {
    public UserProfileResponse toResponse(UserProfile userProfile) {
        return new UserProfileResponse(
                userProfile.getUserId()
        );
    }
}
