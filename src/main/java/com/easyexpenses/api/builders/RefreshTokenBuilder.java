package com.easyexpenses.api.builders;
import com.easyexpenses.api.entities.RefreshToken;
import com.easyexpenses.api.entities.User;

import java.time.Instant;
import java.util.UUID;

public class RefreshTokenBuilder {
    private UUID id;
    private User user;
    private String token;
    private Instant expiresAt;
    private Instant revokedAt;
    private Instant createdAt;


    public RefreshTokenBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public RefreshTokenBuilder user(User user) {
        this.user = user;
        return this;
    }

    public RefreshTokenBuilder token(String token) {
        this.token = token;
        return this;
    }

    public RefreshTokenBuilder expiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public RefreshTokenBuilder revokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
        return this;
    }

    public RefreshTokenBuilder createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public RefreshToken build() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(id);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setRevokedAt(revokedAt);
        refreshToken.setCreatedAt(createdAt);

        if (user != null) {
            user.addNewUserSession(refreshToken);
        }

        return refreshToken;
    }
}