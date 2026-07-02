package com.easyexpenses.api.security;

import com.easyexpenses.api.config.TokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final TokenProperties tokenProperties;
    private final SecretKey secretKey;

    @Autowired
    public JwtService(TokenProperties tokenProperties) {
        this.tokenProperties = tokenProperties;
        this.secretKey = createSecretKey(tokenProperties.secret());
    }

    public GeneratedJwtToken generateToken(JwtGenerationRequest jwtGenerationRequest){
        Date expiration = Date.from(
                Instant.now().plus(tokenProperties.accessTokenExpiration())
        );

        String token = Jwts.builder()
                .subject(jwtGenerationRequest.subject())
                .issuedAt(Date.from(Instant.now()))
                .expiration(expiration)
                .issuer(tokenProperties.issuer())
                .audience().add(tokenProperties.audience()).and()
                .signWith(secretKey)
                .compact();

        long expiresIn =
                Duration.between(
                        Instant.now(),
                        expiration.toInstant()
                ).toSeconds();

        return new GeneratedJwtToken(
                token,
                expiresIn
        );
    }

    public String extractSubject(String token) {
        return tryExtractClaims(token).getSubject();
    }

    public boolean isExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(Date.from(Instant.now()));
    }

    private Date extractExpiration(String token) {
        return tryExtractClaims(token).getExpiration();
    }

    private Claims tryExtractClaims(String token) {
        try{
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch (JwtException ex){
            throw new BadCredentialsException("Invalid auth token provided!");
        }
    }

    public boolean isValid(String token, CustomUserDetails customUserDetails) {
        String subject = extractSubject(token);

        return subject.equals(customUserDetails.getId().toString())
                && !isExpired(token);
    }

    private SecretKey createSecretKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
