package com.wanted.naeil.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CartItemResponse {

    private Long cartItemId;
    private Long courseId;
    private String courseTitle;
    private String instructorName;
    private int price;
    private String thumbnail;
}
