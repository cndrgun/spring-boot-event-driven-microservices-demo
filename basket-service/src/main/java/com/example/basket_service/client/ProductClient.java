package com.example.basket_service.client;

import com.example.basket_service.dto.DtoProduct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class ProductClient {

    private final WebClient webClient;
    private static final String CLIENT_URL = "http://localhost:8081";

    public ProductClient() {
        this.webClient = WebClient.builder()
                .baseUrl(CLIENT_URL)
                .build();
    }

    // Ürün var mı kontrol
    public boolean existsById(Long productId) {
        try {
            return Boolean.TRUE.equals(webClient.get()
                    .uri("/products/{id}/exists", productId)
                    .header("Authorization", getToken())
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                    .block());
        } catch (Exception e) {
            return false;
        }
    }

    // Ürün detaylarını çek
    public DtoProduct getProduct(Long productId) {
        try {
            return webClient.get()
                    .uri("/products/{id}", productId)
                    .header("Authorization", getToken())
                    .retrieve()
                    .bodyToMono(DtoProduct.class)
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    // Header'dan token al, gerekirse "Bearer " ekle
    private String getToken() {
        try {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder
                            .currentRequestAttributes())
                            .getRequest();
            String token = request.getHeader("Authorization");
            if (token != null && !token.startsWith("Bearer ")) {
                token = "Bearer " + token;
            }
            return token;
        } catch (Exception e) {
            return null;
        }
    }
}
