package com.easyexpenses.api.security;

import com.easyexpenses.api.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthService(RegistrationService registrationService, LoginService loginService, RefreshTokenService refreshTokenService) {
        this.registrationService = registrationService;
        this.loginService = loginService;
        this.refreshTokenService = refreshTokenService;
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest){
        return registrationService.register(registrationRequest);
    }

    public AuthResponse authenticate(AuthRequest authRequest){
        return loginService.authenticate(authRequest);
    }

    public RefreshResponse refresh(RefreshRequest refreshRequest){
        return refreshTokenService.refresh(refreshRequest);
    }
}
