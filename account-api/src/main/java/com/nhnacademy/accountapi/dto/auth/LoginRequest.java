package com.nhnacademy.accountapi.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
        @NotBlank
        String userId,

        @NotBlank
        String password
){}
