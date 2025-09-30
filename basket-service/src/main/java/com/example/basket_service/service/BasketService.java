package com.example.basket_service.service;

import com.example.basket_service.client.ProductClient;
import com.example.basket_service.dto.*;
import com.example.basket_service.model.Basket;
import com.example.basket_service.repository.BasketRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BasketService {
    private final ProductClient productClient;
    private final BasketRepository basketRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BasketService(ProductClient productClient, BasketRepository basketRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.productClient = productClient;
        this.basketRepository = basketRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public DtoBasket addBasket(DtoBasketIU dtoBasketIU) {
        try {

            if (!productClient.existsById(dtoBasketIU.getProductId())) {
                throw new RuntimeException("Product bulunamadı");
            }

            // Mevcut basket kontrolü
            Basket basket = basketRepository
                    .findByUserIdAndProductId(dtoBasketIU.getUserId(), dtoBasketIU.getProductId())
                    .orElse(new Basket());

            if (basket.getId() != null) {
                basket.setQuantity(basket.getQuantity() + dtoBasketIU.getQuantity());
            } else {
                BeanUtils.copyProperties(dtoBasketIU, basket);
            }

            Basket savedBasket = basketRepository.save(basket);

            DtoBasket dtoBasket = new DtoBasket();
            BeanUtils.copyProperties(savedBasket, dtoBasket);

            DtoProduct productDto = productClient.getProduct(basket.getProductId());
            dtoBasket.setProduct(productDto);

            return dtoBasket;
        } catch (Exception e) {

            throw new RuntimeException("Sepete ürün ekleme sırasında hata oluştu: " + e.getMessage(), e);
        }
    }

    public DtoBasket removeBasket(DtoBasketIU dtoBasketIU) {

        try {

            // Product kontrol
            if (!productClient.existsById(dtoBasketIU.getProductId())) {
                throw new RuntimeException("Product bulunamadı");
            }

            // Basket entry bul
            Basket basket = basketRepository.findByUserIdAndProductId(dtoBasketIU.getUserId(), dtoBasketIU.getProductId())
                    .orElse(null);

            if (basket == null) {
                throw new RuntimeException("Sepette ürün bulunamadı");
            }

            DtoBasket resDtoBasket = new DtoBasket();

            // Quantity kontrolü ve silme/güncelleme
            if (basket.getQuantity() <= dtoBasketIU.getQuantity()) {

                basketRepository.delete(basket);
                resDtoBasket = null;

            } else {

                basket.setQuantity(basket.getQuantity() - dtoBasketIU.getQuantity());
                Basket savedBasket = basketRepository.save(basket);

                BeanUtils.copyProperties(savedBasket, resDtoBasket);

            }
            if(resDtoBasket != null) {
                DtoProduct productDto = productClient.getProduct(basket.getProductId());
                resDtoBasket.setProduct(productDto);
            }

            return resDtoBasket;

        } catch (Exception e) {

            throw new RuntimeException("Sepetten ürün çıkarma sırasında hata oluştu: " + e.getMessage(), e);
        }
    }

    public List<DtoBasket> getBaskets(Long userId) {

        List<Basket> baskets = basketRepository.findAllByUserId(userId);

        List<DtoBasket> dtoBaskets = new ArrayList<>();
        for (Basket basket : baskets) {
            DtoBasket dto = new DtoBasket();
            BeanUtils.copyProperties(basket, dto);

            DtoProduct productDto = productClient.getProduct(basket.getProductId());
            dto.setProduct(productDto);

            dtoBaskets.add(dto);
        }

        return dtoBaskets;
    }

    public List<DtoBasket> checkout(Long userId) {
        // Sepeti al
        List<Basket> baskets = basketRepository.findAllByUserId(userId);

        if (baskets.isEmpty()) {
            return Collections.emptyList();
        }

        // Event objesi oluştur
        DtoBasketEvent checkoutEvent = new DtoBasketEvent();
        checkoutEvent.setUserId(userId);

        List<DtoBasketItem> items = new ArrayList<>();
        List<DtoBasket> purchased = new ArrayList<>();

        for (Basket basket : baskets) {
            // Checkout event için item oluştur
            DtoBasketItem item = new DtoBasketItem();
            BeanUtils.copyProperties(basket, item);
            items.add(item);

            // DTO oluştur
            DtoBasket dto = new DtoBasket();
            BeanUtils.copyProperties(basket, dto);
            purchased.add(dto);
        }

        checkoutEvent.setItems(items);

        // Kafka'ya tek event olarak gönder
        kafkaTemplate.send("basket-events", checkoutEvent);

        // Sepeti temizle
        basketRepository.deleteAll(baskets);

        return purchased;
    }


}
