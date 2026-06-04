package com.nhnacademy.frontgateway.auth.dto;

public record LoginResponse(
        String userId,
        String email
) {}