package com.easyexpenses.api.builders;
import com.easyexpenses.api.entities.Role;
import com.easyexpenses.api.entities.User;

public class UserBuilder {

    private Long id;
    private String username = "user1";
    private String password = "password";
    private Role role = Role.USER;

    public UserBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder role(Role role) {
        this.role = role;
        return this;
    }

    public User build() {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }
}