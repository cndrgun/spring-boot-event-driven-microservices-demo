package com.example.api_gateway.filter;

import com.example.api_gateway.service.TokenService;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtRedisAuthenticationFilter implements WebFilter {

    private final TokenService tokenService;

    public JwtRedisAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (tokenService.checkTokenVerify(token)) {
                // Token doğru → SecurityContext’e kullanıcı ekle
                String username = Jwts.parser()
                        .setSigningKey(tokenService.getSecretKey().getBytes(StandardCharsets.UTF_8))
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(username, null, List.of());

                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            } else {
                // Token yanlış → 401 dön
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        // Header yoksa → SecurityConfig karar versin (permitAll olabilir)
        return chain.filter(exchange);
    }
}
