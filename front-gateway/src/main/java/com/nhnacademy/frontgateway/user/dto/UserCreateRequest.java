package com.nhnacademy.minidooray.user.dto;

public record UserCreateRequest(
        String userId,
        String email,
        String password
) {}
