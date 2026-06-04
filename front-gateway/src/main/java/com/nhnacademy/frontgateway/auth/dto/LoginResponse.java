package com.nhnacademy.minidooray.auth.dto;

public record LoginResponse(
        String userId,
        String email
) {}