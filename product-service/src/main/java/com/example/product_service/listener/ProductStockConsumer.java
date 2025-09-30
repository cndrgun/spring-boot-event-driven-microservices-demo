package com.example.product_service.listener;

import com.example.product_service.dto.DtoProductStockOutEvent;
import com.example.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductStockConsumer {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    public ProductStockConsumer(ProductService productService) {
        this.productService = productService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(
            topics = "product-stock-out",
            groupId = "product-stock-out-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, Object> record) {
        Object value = record.value();
        System.out.println(">>> Received stock-out event: " + value);

        try {
            // Gelen object'i DTO'ya çevir
            DtoProductStockOutEvent event = objectMapper.convertValue(value, DtoProductStockOutEvent.class);

            productService.decreaseStock(event.getProductId(), event.getQuantity());

        } catch (Exception e) {
            System.err.println("Error processing stock-out event: " + e.getMessage());
        }
    }
}
