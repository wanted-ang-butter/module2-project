package com.wanted.naeil.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateNicknameRequest {
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;
}