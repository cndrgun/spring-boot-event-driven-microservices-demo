package com.example.order_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class DtoBasketEvent {
    private Long userId;

    private List<DtoBasketItem> items;
}
