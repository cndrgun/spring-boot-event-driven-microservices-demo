package com.example.order_service.dto;

import lombok.Data;

@Data
public class DtoBasketItem {
    private Long productId;
    private Integer quantity;
}
