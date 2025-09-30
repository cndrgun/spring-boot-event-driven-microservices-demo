package com.example.basket_service.listener;

import com.example.basket_service.dto.DtoBasketEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestBasketConsumer2 {

    @KafkaListener(topics = "basket-events", groupId = "basket-consumer-group2")
    public void consume(DtoBasketEvent event) {
        System.out.println(">>> Event received: userId=" + event.getUserId()
                + ", productId=" + event.getItems());
    }
}
