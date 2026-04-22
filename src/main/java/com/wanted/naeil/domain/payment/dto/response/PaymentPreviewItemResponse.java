package com.wanted.naeil.domain.payment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentPreviewItemResponse {

    private CourseInfo course;
    private String courseTitle;
    private String courseThumbnail;
    private int price;
    private int discountAmount;
    private int finalPrice;

    @Getter
    @Builder
    public static class CourseInfo {
        private String title;
        private String thumbnail;
    }
}
