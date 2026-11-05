package com.easyexpenses.api.dtos.userprofile.response;

public record NotCompletedUserProfileResponse(boolean isCompleted) implements UserProfileResponse{
}