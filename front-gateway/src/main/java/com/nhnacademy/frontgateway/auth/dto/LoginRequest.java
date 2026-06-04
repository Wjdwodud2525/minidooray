package com.nhnacademy.frontgateway.auth.dto;

public record LoginRequest(
        String userId,
        String password
) {}