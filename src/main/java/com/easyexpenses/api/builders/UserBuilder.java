package com.easyexpenses.api.builders;


import com.easyexpenses.api.entities.User;

import java.time.Instant;

public class UserBuilder {
    private String identityIssuer;
    private String identitySubject;
    private final Instant createdAt = Instant.now();

    public UserBuilder identityIssuer(String identityIssuer) {
        this.identityIssuer = identityIssuer;
        return this;
    }

    public UserBuilder identitySubject(String identitySubject) {
        this.identitySubject = identitySubject;
        return this;
    }

    public User build() {
        User user = new User();
        user.setIdentityIssuer(identityIssuer);
        user.setIdentitySubject(identitySubject);
        user.setCreatedAt(createdAt);

        return user;
    }
}
