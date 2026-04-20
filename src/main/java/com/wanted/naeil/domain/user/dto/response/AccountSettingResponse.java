package com.wanted.naeil.domain.user.dto.response;

import com.wanted.naeil.domain.user.entity.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountSettingResponse {
    private String username;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String profileImg;
    private Role role;
}
