package com.wanted.naeil.domain.user.dto.response;


import com.wanted.naeil.domain.user.entity.enums.Role;
import com.wanted.naeil.domain.user.entity.enums.UserStatus;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class LoginUserDTO {

    private Long userId;
    private String username;
    private String name;
    private String email;
    private String password;
    private Role role;
    private UserStatus status;
    private String nickname;
    private String profileImg;
}