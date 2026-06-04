package com.nhnacademy.account.dto.user;

import com.nhnacademy.account.entity.UserStatus;

public record UserResponse (
        String userId,
        String email,
        UserStatus status
){}
