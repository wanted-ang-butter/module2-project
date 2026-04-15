package com.wanted.naeil.domain.course.dto.request;

import com.wanted.naeil.domain.course.entity.Category;
import org.springframework.web.multipart.MultipartFile;

public record CreateCourseRequest(
        // TODO : instructorId 테스트용 추가, 승재 코드 병합 후 삭제
        Long instructorId,
        String title,
        Long categoryId,
        String description,
        int price,
        MultipartFile thumbnail
        //TODO : CreateSectionRequset 생성 후 추가
) {
}
