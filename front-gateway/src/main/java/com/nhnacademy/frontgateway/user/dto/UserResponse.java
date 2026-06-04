package com.nhnacademy.frontgateway.user.dto;

public record UserResponse(
        String userId,
        String email,
        UserStatus status
) {}
