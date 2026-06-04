package com.nhnacademy.minidooray.user.dto;

public record UserUpdateRequest(
        String email,
        String password
) {}
