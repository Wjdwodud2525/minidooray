package com.nhnacademy.account.dto.user;

import lombok.Builder;

@Builder
public record UserExistsResponse(
        Boolean exists
) {
}
