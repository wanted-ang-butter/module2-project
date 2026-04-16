package com.wanted.naeil.domain.course.dto.request;

import com.wanted.naeil.domain.course.entity.SectionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class UploadSectionRequest {

     @NotBlank(message = "섹션 제목을 입력해주세요.")
     private String title;

     @NotNull(message = "강의 영상 파일은 필수입니다.")
     private MultipartFile videoFile;

     @NotNull(message = "재생 시간을 입력해주세요. (00:00:00)")
     @DateTimeFormat(pattern = "HH:mm:ss")
     private LocalTime playTime;
     private Boolean isFree;
     private Boolean isActive;
     private MultipartFile attachmentFile;
}
