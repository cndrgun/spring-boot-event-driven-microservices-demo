package com.example.order_service.listener;

import com.example.order_service.dto.DtoBasketEvent;
import com.example.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BasketEventConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    BasketEventConsumer(ObjectMapper objectMapper, OrderService orderService) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "basket-events", groupId = "order-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, Object> record) {


        Object value = record.value();

        System.out.println(">>> Value class: " + value.getClass().getName());
        System.out.println(">>> Value content: " + value);

        // Eğer DTO'ya çevirmek istiyorsan ObjectMapper kullan
        try {

            DtoBasketEvent event = objectMapper.convertValue(value, DtoBasketEvent.class);
            orderService.createOrderFromBasketEvent(event);

        } catch (Exception e) {
            System.err.println("Error processing event: " + e.getMessage());
        }
    }
}
