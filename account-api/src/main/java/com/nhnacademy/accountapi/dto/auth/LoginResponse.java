package com.nhnacademy.accountapi.dto.auth;

public record LoginResponse(
        String userId,
        String email
) {}
