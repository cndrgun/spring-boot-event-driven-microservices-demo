package com.example.order_service.service;

import com.example.order_service.Repository.OrderRepository;
import com.example.order_service.dto.DtoBasketEvent;
import com.example.order_service.dto.DtoOrder;
import com.example.order_service.dto.DtoOrderProduct;
import com.example.order_service.dto.DtoProductStockOutEvent;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderProduct;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<DtoOrder> findAllByUserId(Long userId) {

        List<Order> orders = orderRepository.findAllByUserId(userId);
        List<DtoOrder> dtoOrders = new ArrayList<>();
        orders.forEach(order -> {

            DtoOrder dtoOrder = new DtoOrder();
            dtoOrder.setOrderId(order.getId());
            dtoOrder.setCreatedAt(order.getCreatedAt());

            List<DtoOrderProduct> dtoOrderProducts = new ArrayList<>();
            order.getOrderProducts().forEach(orderProduct -> {
                DtoOrderProduct dtoOrderProduct = new DtoOrderProduct();
                BeanUtils.copyProperties(orderProduct, dtoOrderProduct);
                dtoOrderProducts.add(dtoOrderProduct);
            });

            dtoOrder.setOrderProducts(dtoOrderProducts);
            dtoOrders.add(dtoOrder);
        });
        return dtoOrders;

    }

    public void createOrderFromBasketEvent(DtoBasketEvent event) {
        Order order = new Order();
        order.setUserId(event.getUserId());

        List<OrderProduct> products = event.getItems().stream()
                .map(item -> {
                    OrderProduct op = new OrderProduct();
                    op.setProductId(item.getProductId());
                    op.setQuantity(item.getQuantity());
                    op.setOrder(order);
                    return op;
                })
                .collect(Collectors.toList());

        order.setOrderProducts(products);

        Order saveOrder = orderRepository.save(order);
        saveOrder.getOrderProducts().forEach(orderProduct -> {
            DtoProductStockOutEvent stockOutProduct = new DtoProductStockOutEvent();
            BeanUtils.copyProperties(orderProduct, stockOutProduct);

            kafkaTemplate.send("product-stock-out", stockOutProduct);
            System.out.println(">>> Sent stock-out event for productId=" + orderProduct.getProductId());
        });
    }
}
