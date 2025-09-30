package com.example.api_gateway.config;

import com.example.api_gateway.filter.JwtRedisAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtRedisAuthenticationFilter jwtRedisAuthenticationFilter;

    public SecurityConfig(JwtRedisAuthenticationFilter jwtRedisAuthenticationFilter) {
        this.jwtRedisAuthenticationFilter = jwtRedisAuthenticationFilter;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/user/login", "/user/register", "/token/**").permitAll()
                        .anyExchange().authenticated()
                )
                // Custom filter ekliyoruz
                .addFilterAt(jwtRedisAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
