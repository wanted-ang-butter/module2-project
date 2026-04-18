package com.wanted.naeil.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FindPasswordResponse {
    private String tempPassword;
}
