package com.nhnacademy.accountapi.dto.user;

import lombok.Builder;

@Builder
public record UserExistsResponse(
        Boolean exists
) {
}
