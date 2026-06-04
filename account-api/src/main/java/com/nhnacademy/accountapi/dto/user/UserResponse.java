package com.nhnacademy.accountapi.dto.user;

import com.nhnacademy.accountapi.entity.UserStatus;

public record UserResponse (
        String userId,
        String email,
        UserStatus status
){

}
