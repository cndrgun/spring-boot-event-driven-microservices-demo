package com.example.authentication.service;

import com.example.authentication.dto.DtoAuthToken;
import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public DtoAuthToken loginUser(String username, String password) {

        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword() != null && passwordEncoder.matches(password, user.getPassword())) {

            try {

                DtoAuthToken token = tokenService.generateToken(user.getUsername(), user.getId());
                return token;

            } catch (Exception e) {

                return null;

            }

        }

        return null;
    }

    public boolean logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);
        return tokenService.revokeToken(token);
    }
}
