package com.devops.auth.dto;

public record AuthResponse(String token, String username, String role, String message) {}
