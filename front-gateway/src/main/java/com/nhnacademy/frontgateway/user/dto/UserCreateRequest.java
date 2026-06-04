package com.nhnacademy.frontgateway.user.dto;

public record UserCreateRequest(
        String userId,
        String email,
        String password
) {}
