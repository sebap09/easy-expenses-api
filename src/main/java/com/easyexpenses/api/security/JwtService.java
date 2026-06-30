package com.easyexpenses.api.security;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    @Autowired
    private final SecretKey secretKey;

    public JwtService(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(JwtGenerationRequest jwtGenerationRequest){
        Date expiration = Date.from(
                Instant.now().plus(jwtGenerationRequest.expiresIn())
        );
        return Jwts.builder()
                .subject(jwtGenerationRequest.subject())
                .issuedAt(new Date())
                .expiration(expiration)
                .issuer(jwtGenerationRequest.issuer())
                .audience().add(jwtGenerationRequest.audience()).and()
                .signWith(secretKey)
                .compact();
    }

    public String extractSubject(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private Date extractExpiration(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    public boolean isExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    public boolean isValid(String token, CustomUserDetails customUserDetails) {
        String subject = extractSubject(token);

        return subject.equals(customUserDetails.getId().toString())
                && !isExpired(token);
    }
}
