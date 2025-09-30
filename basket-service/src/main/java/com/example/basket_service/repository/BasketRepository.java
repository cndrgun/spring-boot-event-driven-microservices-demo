package com.example.basket_service.repository;

import com.example.basket_service.model.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<Basket,Long> {
    Optional<Basket> findByUserIdAndProductId(Long userId, Long productId);

    List<Basket> findAllByUserId(Long userId);
}
