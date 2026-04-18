package com.wanted.naeil.domain.course.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class CourseUpdateRequest {

    @NotBlank(message = "강의 제목은 필수입니다.")
    @Size(max = 100, message = "강의 제목은 100자 이내여야 합니다.")
    private String title;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;

    @NotBlank(message = "강의 설명을 입력해주세요.")
    private String description;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private int price;

    private MultipartFile thumbnail;
}
