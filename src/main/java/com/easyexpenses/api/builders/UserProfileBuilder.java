package com.easyexpenses.api.builders;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.entities.UserProfile;

public class UserProfileBuilder {

    private User user;

    public UserProfileBuilder user(User user){
        this.user =  user;
        return this;
    }

    public UserProfileBuilder user(Long id){
        this.user = new UserBuilder()
                .id(id)
                .identityIssuer("https://auth.example.com/realms/myrealm")
                .identitySubject(UserBuilder.getRandomSubject())
                .build();
        return this;
    }

    public UserProfileBuilder user(){
        this.user = new UserBuilder()
                .identityIssuer("https://auth.example.com/realms/myrealm")
                .identitySubject(UserBuilder.getRandomSubject())
                .build();
        return this;
    }

    public UserProfile build() {
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setUserId(user.getId());
        user.setUserProfile(userProfile);
        return userProfile;
    }
}