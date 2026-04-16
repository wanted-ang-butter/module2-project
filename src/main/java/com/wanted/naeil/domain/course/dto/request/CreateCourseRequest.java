package com.wanted.naeil.domain.course.dto.request;

import com.wanted.naeil.domain.course.entity.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateCourseRequest {
    // TODO : 임시로 강사 ID 저장
    private Long instructorId;
    private String title;
    private Long categoryId;
    private String description;
    private int price;
    private MultipartFile thumbnail;

    // 💡 핵심: 스프링이 데이터를 담을 수 있도록 ArrayList를 초기화해 줍니다.
    private List<UploadSectionRequest> sections = new ArrayList<>();
}
