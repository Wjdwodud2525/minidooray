package com.nhnacademy.minidooray.auth.dto;

public record LoginRequest(
        String userId,
        String password
) {}