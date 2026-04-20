package com.wanted.naeil.domain.live.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateLiveLectureRequest {

    @NotBlank(message = "실시간 강의 제목은 필수 입력 사항입니다.")
    @Size(max = 100, message = "강의 제목은 100자 이내여야 합니다.")
    private String title;

    @NotBlank(message = "강의 설명은 필수 입력 사항입니다.")
    private String description;

    @NotNull(message = "수강 정원은 필수 입력 사항입니다.")
    @Min(value = 1, message = "수강 정원은 최소 1명 이상이어야 합니다.")
    @Max(value = 100, message = "신청 가능한 최대 수강 정원은 100명입니다.")
    private Integer maxCapacity;

    @NotNull(message = "강의 시작 일시는 필수 입력 사항입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startAt;

    @NotNull(message = "강의 종료 일시는 필수 입력 사항입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endAt;

    @NotNull(message = "예약 시작 일시는 필수 입력 사항입니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime reservationStartAt;

    @NotBlank(message = "방송 URL은 필수 입력 사항입니다.")
    @Size(max = 500, message = "방송 URL은 500자 이내여야 합니다.")
    private String streamingUrl;

}
