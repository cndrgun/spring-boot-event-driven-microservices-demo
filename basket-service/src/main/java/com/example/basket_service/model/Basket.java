package com.example.basket_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "basket")
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "user_id boş olamaz")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "product_id boş olamaz")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull(message = "quantity boş olamaz!")
    @Positive(message = "quantity sıfırdan büyük olmalı")
    @Column(nullable = false)
    private Integer quantity = 1;

}
