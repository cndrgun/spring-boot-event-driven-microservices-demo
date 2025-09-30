package com.example.basket_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DtoBasketIU {

    private Long userId;

    @NotNull(message = "productId boş olamaz")
    private Long productId;

    @Positive(message = "quantity sıfırdan büyük olmalı")
    private Integer quantity = 1;
}
