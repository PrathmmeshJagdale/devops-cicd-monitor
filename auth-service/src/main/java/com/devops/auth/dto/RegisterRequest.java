package com.devops.auth.dto;

public record RegisterRequest(String username, String password, String email, String role) {}
