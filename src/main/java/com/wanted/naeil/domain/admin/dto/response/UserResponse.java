package com.wanted.naeil.domain.admin.dto.response;


import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.entity.enums.Role;
import com.wanted.naeil.domain.user.entity.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private Role role;
    private UserStatus status;
    private int warningCount;
    private LocalDate birthDate;


    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .warningCount(user.getWarningCount())
                .birthDate(user.getBirthDate())
                .build();
    }

}
