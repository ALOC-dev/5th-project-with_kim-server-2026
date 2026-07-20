package com.with_kim.aloc_study.security;

import com.with_kim.aloc_study.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey key;

    @Value("${jwt.expiration-time}")
    private long accessExpirationTime;

    @Value("${jwt.refresh-expiration-time}")
    private long refreshaccessExpirationTime;

    @PostConstruct
    protected void init() {
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public String generateAccessToken(Users user) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("loginId", user.getLoginId())
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Users user) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + refreshaccessExpirationTime);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("loginId", user.getLoginId())
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiredDate)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        String username = getUsername(token);

        UserDetails userDetails = User.builder()
                .username(username)
                .password("")
                .authorities("ROLE_USER")
                .build();

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getExpirationTime(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .getTime();
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getUserId(String token) {
        return parseClaims(token)
                .getSubject();
    }

    public Long getUserIdAsLong(String token) {
        return Long.valueOf(getUserId(token));
    }

    public String getLoginId(String token) {
        return parseClaims(token)
                .get("loginId", String.class);
    }

    public String getTokenType(String token) {
        return parseClaims(token)
                .get("type", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
