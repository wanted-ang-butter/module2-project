package com.wanted.naeil.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CartPageResponse {

    private List<CartItemResponse> cartItems;
    private CartSummaryResponse summary;
}