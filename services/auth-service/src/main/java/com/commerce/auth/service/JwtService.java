package com.commerce.auth.service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

@Service
public class JwtService {

  private final SecretKey key = Keys.hmacShaKeyFor(
      System.getenv().getOrDefault("JWT_SECRET",
          "change-me-very-secret-should-be-32+chars").getBytes()
  );

  private final long accessTtlSeconds = 60 * 15;

  public String generateAccessToken(Long userId, String email, String rolesCsv) {
    Instant now = Instant.now();

    return Jwts.builder()
        .subject(String.valueOf(userId))
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
        .claims(Map.of("email", email, "roles", rolesCsv))
        .signWith(key)
        .compact();
  }

  public Jws<Claims> parseToken(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token);
  }
}
