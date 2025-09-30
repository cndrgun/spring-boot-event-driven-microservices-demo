package com.example.basket_service.controller;

import com.example.basket_service.dto.DtoBasket;
import com.example.basket_service.dto.DtoBasketIU;
import com.example.basket_service.service.BasketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/basket")
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @PostMapping("/add")
    public ResponseEntity<DtoBasket> addBasket(@Valid @RequestBody DtoBasketIU dtoBasketIU, @RequestAttribute Long userId){

        dtoBasketIU.setUserId(userId);

        DtoBasket dtoBasket = basketService.addBasket(dtoBasketIU);
        return ResponseEntity.ok(dtoBasket);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<DtoBasket> removeBasket(@Valid @RequestBody DtoBasketIU dtoBasketIU, @RequestAttribute Long userId){
        dtoBasketIU.setUserId(userId);
        DtoBasket dtoBasket = basketService.removeBasket(dtoBasketIU);
        return ResponseEntity.ok(dtoBasket);
    }

    @GetMapping("/getBaskets")
    public ResponseEntity<List<DtoBasket>> getBaskets(@RequestAttribute Long userId){
        List<DtoBasket> baskets = basketService.getBaskets(userId);
        return ResponseEntity.ok(baskets);
    }

    @GetMapping("/checkout")
    public ResponseEntity<List<DtoBasket>> checkout(@RequestAttribute Long userId) {
        List<DtoBasket> purchased = basketService.checkout(userId);
        return ResponseEntity.ok(purchased);
    }
}
