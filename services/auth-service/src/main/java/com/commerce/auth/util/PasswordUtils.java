package com.commerce.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordUtils {
  private static final BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
  public static String hash(String raw) { return enc.encode(raw); }
  public static boolean matches(String raw, String hash) { return enc.matches(raw, hash); }
}
