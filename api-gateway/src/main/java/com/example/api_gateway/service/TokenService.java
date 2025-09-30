package com.example.api_gateway.service;

import com.example.api_gateway.util.RedisUtil;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service

public class TokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.redis.db}")
    private int redisDbIndex;

    @Autowired
    private RedisUtil redisUtil;

    public boolean checkTokenVerify(String token) {
        try {
            //JWT doğrulaması
            Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token);

            //Redis kontrolü → token aktif mi?
            String username = redisUtil.get(redisDbIndex, "access:" + token, String.class);

            return username != null;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            // JWT geçersiz veya Redis'te yoksa
            return false;
        }
    }

    public String getSecretKey() {
        return secretKey;
    }

}
