package com.easyexpenses.api.security;

import com.easyexpenses.api.builders.RefreshTokenBuilder;
import com.easyexpenses.api.config.TokenProperties;
import com.easyexpenses.api.dtos.RefreshRequest;
import com.easyexpenses.api.dtos.RefreshResponse;
import com.easyexpenses.api.dtos.RevokeRequest;
import com.easyexpenses.api.entities.RefreshToken;
import com.easyexpenses.api.entities.User;
import com.easyexpenses.api.repositories.RefreshTokenRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class RefreshTokenService {
    private final JwtService jwtService;
    private final TokenProperties tokenProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(JwtService jwtService, TokenProperties tokenProperties, RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.tokenProperties = tokenProperties;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void revoke(RevokeRequest revokeRequest) {
        findValidateAndRevoke(revokeRequest.refreshToken());
    }

    public RefreshResponse refresh(RefreshRequest refreshRequest){
        RefreshToken oldRefreshToken = findValidateAndRevoke(refreshRequest.refreshToken());

        GeneratedRefreshToken newRefreshToken = generateRefreshToken(oldRefreshToken.getUser());
        //Can contain other claims in the future
        JwtGenerationRequest jwtGenerationRequest = new JwtGenerationRequest(
                newRefreshToken.user().getId().toString());
        String accessToken = jwtService.generateToken(jwtGenerationRequest).token();


        return new RefreshResponse(
                accessToken,
                newRefreshToken.token()
        );
    }

    public GeneratedRefreshToken generateRefreshToken(User user){
        Instant expiration = Instant.now().plus(tokenProperties.refreshTokenExpiration());
        String opaqueToken = generateTokenValue();
        RefreshToken refreshToken = new RefreshTokenBuilder()
                .user(user)
                .token(DigestUtils.sha256Hex(opaqueToken))
                .expiresAt(expiration)
                .createdAt(Instant.now())
                .build();

        refreshTokenRepository.save(refreshToken);
        return new GeneratedRefreshToken(opaqueToken, refreshToken.getUser());
    }

    private RefreshToken findValidateAndRevoke(String token) {
        RefreshToken refreshToken = findByTokenHash(token);
        validate(refreshToken);

        refreshToken.setRevokedAt(Instant.now());
        return refreshTokenRepository.save(refreshToken);
    }

    private String generateTokenValue(){
        byte[] bytes = new byte[64]; // 512 bits
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private RefreshToken findByTokenHash(String token){
        return refreshTokenRepository.findByToken(DigestUtils.sha256Hex(token)).orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
    }

    private void validate(RefreshToken refreshToken){
        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        if (refreshToken.getRevokedAt() != null) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        if (refreshToken.getUser() == null) {
            throw new BadCredentialsException("Invalid refresh token");
        }
    }
}
