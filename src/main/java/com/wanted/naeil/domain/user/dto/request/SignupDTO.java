package com.wanted.naeil.domain.user.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@ToString

public class SignupDTO {

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,15}$", message = "영문, 숫자 혹은 언더바(_) 조합 4자 이상 입력해 주세요.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^*+=-]).{8,}$", message = "영문, 숫자, 특수문자(!@#$%^*+=-) 조합 8자 이상")
    private String password;
    private String passwordConfirm;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$", message = "010-0000-0000 형식으로 입력해주세요.")
    private String phone;

    @NotNull(message = "생년월일은 필수 입력 값입니다.")
    private LocalDate birthDate;

    private MultipartFile profileImg;
}