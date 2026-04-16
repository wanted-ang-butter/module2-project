package com.wanted.naeil.domain.course.dto.request;

import com.wanted.naeil.domain.course.entity.SectionStatus;
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
     private String title;
     private MultipartFile videoFile;
     @DateTimeFormat(pattern = "HH:mm:ss")
     private LocalTime playTime;
     private Boolean isFree;
     private Boolean isActive;
     private MultipartFile attachmentFile;
}
