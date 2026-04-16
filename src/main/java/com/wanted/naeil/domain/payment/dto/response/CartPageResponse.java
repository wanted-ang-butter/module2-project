package com.wanted.naeil.domain.payment.dto.response;

import com.wanted.naeil.domain.payment.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CartPageResponse {
    private List<CartItemResponse> cartItems;
    private List<Long> selectedCartItemIds;
    private int totalAmount;
    private int creditBalance;
    private boolean canCheckout;
}