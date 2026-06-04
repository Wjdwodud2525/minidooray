package com.nhnacademy.account.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        String password
) {}
