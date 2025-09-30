package com.example.order_service.dto;

import lombok.Data;

@Data
public class DtoOrderProduct {
    private Long productId;
    private int quantity;
}
