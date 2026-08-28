package com.easyexpenses.api.builders;
import com.easyexpenses.api.entities.UserProfile;

public class UserProfileBuilder {

    private Long id;

    public UserProfileBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public UserProfile build() {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(id);
        return userProfile;
    }
}