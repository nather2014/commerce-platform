package com.commerce.auth.service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  private final Key key = Keys.hmacShaKeyFor(System.getenv().getOrDefault("JWT_SECRET","change-me-very-secret-should-be-32+chars").getBytes());
  private final long accessTtlSeconds = 60 * 15; // 15 min

  public String generateAccessToken(Long userId, String email, String rolesCsv) {
    Instant now = Instant.now();
    return Jwts.builder()
      .setSubject(String.valueOf(userId))
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plusSeconds(accessTtlSeconds)))
      .addClaims(Map.of("email", email, "roles", rolesCsv))
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public Jws<Claims> parseToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
  }
}
