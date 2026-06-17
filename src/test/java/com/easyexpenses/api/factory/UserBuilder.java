package com.easyexpenses.api.factory;
import com.easyexpenses.api.entities.User;

public class UserBuilder {

    private String username = "user1";
    private String password = "password";

    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public User build() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
}