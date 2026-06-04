package com.nhnacademy.minidooray.user.dto;

public record UserResponse(
        String userId,
        String email,
        UserStatus status
) {}
