package com.wanted.naeil.domain.user.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ApplyInstructorRequest {
    private Long categoryId;       // 카테고리 선택
    private String title;          // 신청 제목
    private String introduction;   // 소개
    private String career;         // 경력
    private String accountNumber;  // 계좌번호
    private List<MultipartFile> proofFiles;  // 증명파일
    private MultipartFile faceImg;    // 얼굴사진
}
