package com.easyexpenses.api.builders;


import com.easyexpenses.api.entities.User;

import java.time.Instant;
import java.util.UUID;

public class UserBuilder {
    private Long id;
    private String identityIssuer;
    private String identitySubject;
    private final Instant createdAt = Instant.now();

    public static String getRandomSubject(){
        return UUID.randomUUID().toString();
    }

    public UserBuilder id(Long id) {
        this.id = id;
        return this;
    }

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
        user.setId(id);
        user.setIdentityIssuer(identityIssuer);
        user.setIdentitySubject(identitySubject);
        user.setCreatedAt(createdAt);
        return user;
    }
}
