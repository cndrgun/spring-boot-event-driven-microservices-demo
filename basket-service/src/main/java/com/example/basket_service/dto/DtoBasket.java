package com.example.basket_service.dto;
import lombok.Data;

@Data
public class DtoBasket {
    private Long id;
    private Integer quantity;
    private DtoProduct product;
}
