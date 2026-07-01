package com.easyexpenses.api.security;

import com.easyexpenses.api.dtos.AuthRequest;
import com.easyexpenses.api.dtos.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public LoginService(AuthenticationManager authenticationManager, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse authenticate(AuthRequest authRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.username(),
                        authRequest.password()
                )
        );
        CustomUserDetails customUserDetails = Objects.requireNonNull((CustomUserDetails) authentication.getPrincipal());

        //Can contain other claims in the future
        JwtGenerationRequest jwtGenerationRequest = new JwtGenerationRequest(
                customUserDetails.getId().toString()
        );

        GeneratedJwtToken accessToken = jwtService.generateToken(jwtGenerationRequest);
        GeneratedRefreshToken refreshToken = refreshTokenService.generateRefreshToken(customUserDetails.getUser());

        return new AuthResponse(
                accessToken.token(),
                "Bearer",
                accessToken.expiresIn(),
                refreshToken.token()
        );
    }
}
