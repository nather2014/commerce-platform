package com.commerce.auth.service;

import com.commerce.auth.dto.LoginRequest;
import com.commerce.auth.dto.LoginResponse;
import com.commerce.auth.dto.RegisterRequest;
import com.commerce.auth.entity.RefreshToken;
import com.commerce.auth.entity.User;
import com.commerce.auth.repository.RefreshTokenRepository;
import com.commerce.auth.repository.UserRepository;
import com.commerce.auth.util.PasswordUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {
  private final UserRepository userRepo;
  private final RefreshTokenRepository refreshRepo;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepo, RefreshTokenRepository refreshRepo, JwtService jwtService){
    this.userRepo = userRepo; this.refreshRepo = refreshRepo; this.jwtService = jwtService;
  }

  @Transactional
  public void register(RegisterRequest r) {
    if (userRepo.existsByEmail(r.email())) throw new IllegalArgumentException("email-taken");
    User u = new User();
    u.setEmail(r.email());
    u.setPasswordHash(PasswordUtils.hash(r.password()));
    u.setRoles(Set.of("ROLE_USER"));
    userRepo.save(u);
  }

  public LoginResponse login(LoginRequest req) {
    User u = userRepo.findByEmail(req.email()).orElseThrow(() -> new IllegalArgumentException("invalid-creds"));
    if (!PasswordUtils.matches(req.password(), u.getPasswordHash())) throw new IllegalArgumentException("invalid-creds");
    String rolesCsv = String.join(",", u.getRoles());
    String access = jwtService.generateAccessToken(u.getId(), u.getEmail(), rolesCsv);
    RefreshToken rt = new RefreshToken();
    rt.setToken(UUID.randomUUID().toString());
    rt.setUser(u);
    rt.setExpiresAt(Instant.now().plusSeconds(60L * 60 * 24 * 30)); // 30 days
    refreshRepo.save(rt);
    return new LoginResponse(access, rt.getToken(), "Bearer");
  }

  public void logout(Long userId) { refreshRepo.deleteByUserId(userId); }
}
