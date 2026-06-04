package com.nhnacademy.frontgateway.user.dto;

public record UserUpdateRequest(
        String email,
        String password
) {}
