package com.wanted.naeil.domain.course.dto.request;

import com.wanted.naeil.domain.course.entity.enums.CourseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CourseStatusUpdateRequest {

    @NotNull(message = "변경할 코스 상태를 입력해주세요.")
    private CourseStatus status;
}
