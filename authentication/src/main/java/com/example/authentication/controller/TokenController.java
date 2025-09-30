package com.example.authentication.controller;

import com.example.authentication.dto.DtoAuthToken;
import com.example.authentication.service.AuthenticationService;
import com.example.authentication.service.TokenService;
import com.example.authentication.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/token")
@RestController
public class TokenController {

    private TokenService tokenService;

    TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping(path = "/verify")
    public ResponseEntity<String> verifyToken(@RequestParam("token") String token) {
        boolean isValid = tokenService.checkTokenVerify(token);
        return isValid ? ResponseEntity.ok("token valid") : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<DtoAuthToken> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        DtoAuthToken newTokens = tokenService.refreshTokens(refreshToken);
        return newTokens != null ? ResponseEntity.ok(newTokens) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
