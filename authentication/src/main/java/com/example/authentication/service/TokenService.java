package com.example.authentication.service;

import com.example.authentication.dto.DtoAuthToken;
import com.example.authentication.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.refreshExpiration}")
    private long refreshExpirationTime;

    @Value("${jwt.redis.db}")
    private int redisDbIndex;

    @Autowired
    private RedisUtil redisUtil;

    public DtoAuthToken generateToken(String username, Long userId) {

        String accessToken = generateAccessToken(username, userId);
        String refreshToken = generateRefreshToken(username, userId);

        return new DtoAuthToken(accessToken, refreshToken);
    }
    //Jwt Token Oluştur + Redise Kayıt
    public String generateAccessToken(String username, Long userId) {

        long expirationMillis = Duration.ofMinutes(expirationTime).toMillis();

        String token = Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        // Redis'e kaydet
        saveAccessToken( token, username);

        return token;

    }

    public String generateRefreshToken(String username, Long userId) {
        String refreshToken = UUID.randomUUID().toString(); // random string refresh token
        saveRefreshToken(username, userId, refreshToken);
        return refreshToken;
    }

    public void saveAccessToken(String token, String username) {

        String oldToken = redisUtil.get(redisDbIndex, "user:" + username + ":access", String.class);
        if (oldToken != null) {
            redisUtil.delete(redisDbIndex, "access:" + oldToken); // eski token sil
        }
        // access tokeni key olarak kaydet
        redisUtil.set(redisDbIndex, "access:" + token, username, Duration.ofMinutes(expirationTime));
        // username → access token map’i
        redisUtil.set(redisDbIndex, "user:" + username + ":access", token, Duration.ofMinutes(expirationTime));
    }

    private void saveRefreshToken(String username,Long userId, String refreshToken) {
        // eski refresh token varsa sil
        String oldRefresh = redisUtil.get(redisDbIndex, "user:" + username + ":refresh", String.class);
        if (oldRefresh != null) {
            redisUtil.delete(redisDbIndex, "refresh:" + oldRefresh);
        }
        String value = userId + ":" + username;

        redisUtil.set(redisDbIndex, "refresh:" + refreshToken, value, Duration.ofMinutes(refreshExpirationTime));
        redisUtil.set(redisDbIndex, "user:" + username + ":refresh", refreshToken, Duration.ofMinutes(refreshExpirationTime));
    }

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

    public boolean revokeToken(String token) {

        String value = redisUtil.get(redisDbIndex, "access:" + token, String.class);
        if (value == null) return false;

        String[] parts = value.split(":");
        String username = parts[1];

        // Access token sil
        redisUtil.delete(redisDbIndex, "access:" + token);
        redisUtil.delete(redisDbIndex, "user:" + username + ":access");

        // Refresh token sil
        String refreshToken = redisUtil.get(redisDbIndex, "user:" + username + ":refresh", String.class);
        if (refreshToken != null) {
            redisUtil.delete(redisDbIndex, "refresh:" + refreshToken);
            redisUtil.delete(redisDbIndex, "user:" + username + ":refresh");
        }

        return true;
    }

    public DtoAuthToken refreshTokens(String refreshToken) {
        // Redis'ten refresh token doğrula
        String value = redisUtil.get(redisDbIndex, "refresh:" + refreshToken, String.class);
        if (value == null) {
            return null;
        }

        String[] parts = value.split(":");
        Long userId = Long.parseLong(parts[0]);
        String username = parts[1];

        // Yeni access & refresh token üret
        String newAccessToken = generateAccessToken(username, userId);
        String newRefreshToken = generateRefreshToken(username, userId);

        return new DtoAuthToken(newAccessToken, newRefreshToken);
    }

}
