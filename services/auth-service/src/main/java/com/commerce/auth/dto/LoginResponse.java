package com.commerce.auth.dto;

public record LoginResponse(String accessToken, String refreshToken, String tokenType) {}
