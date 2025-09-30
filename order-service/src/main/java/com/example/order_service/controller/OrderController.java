package com.example.order_service.controller;

import com.example.order_service.dto.DtoOrder;
import com.example.order_service.model.Order;
import com.example.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/orders")
@RestController
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order-list")
    public ResponseEntity<List<DtoOrder>> getOrderByUserId(@RequestAttribute Long userId)
    {
        List<DtoOrder> orders = orderService.findAllByUserId(userId);
        return ResponseEntity.ok(orders);
    }

}
