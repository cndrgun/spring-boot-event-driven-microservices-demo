package com.example.product_service.dto;

import lombok.Data;

@Data
public class DtoProductStockOutEvent {
    private Long productId;
    private int quantity;
}
