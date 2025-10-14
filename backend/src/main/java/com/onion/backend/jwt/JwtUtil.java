package com.onion.backend.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Base64;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey key;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(username) // setSubject() -> subject()
                .issuedAt(now)      // setIssuedAt() -> issuedAt()
                .expiration(expiration) // setExpiration() -> expiration()
                .signWith(key)      // .signWith(SignatureAlgorithm.HS256, secretKey) -> .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key) // setSigningKey() -> verifyWith()
                    .build()
                    .parseSignedClaims(token); // parseClaimsJws() -> parseSignedClaims()
            return true;
        } catch (Exception e) {
            // 토큰 유효성 검증 실패 시 예외 처리
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key) // setSigningKey() -> verifyWith()
                .build()
                .parseSignedClaims(token) // parseClaimsJws() -> parseSignedClaims()
                .getPayload(); // getBody() -> getPayload()
        return claims.getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getBody();
        return claims.getExpiration();
    }
}