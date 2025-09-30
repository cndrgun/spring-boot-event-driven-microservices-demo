package com.example.authentication.dto;

import lombok.Data;

@Data
public class DtoAuthToken {
    public DtoAuthToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    private String accessToken;
    private String refreshToken;
}
