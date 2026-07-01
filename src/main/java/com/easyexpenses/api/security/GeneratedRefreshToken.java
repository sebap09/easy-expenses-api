package com.easyexpenses.api.security;

import com.easyexpenses.api.entities.User;

public record GeneratedRefreshToken(String token, User user) {
}
