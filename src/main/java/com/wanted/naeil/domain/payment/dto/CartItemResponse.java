package com.wanted.naeil.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CartItemResponse {

    private Long cartItemId;
    private Long courseId;
    private String courseTitle;
    private String instructorName;
    private int price;
    private String thumbnail;
}
