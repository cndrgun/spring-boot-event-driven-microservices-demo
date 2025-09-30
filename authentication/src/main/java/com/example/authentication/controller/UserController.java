package com.example.authentication.controller;

import com.example.authentication.dto.DtoAuthToken;
import com.example.authentication.dto.DtoUser;
import com.example.authentication.dto.DtoUserIU;
import com.example.authentication.dto.DtoUserLogin;
import com.example.authentication.model.User;
import com.example.authentication.service.AuthenticationService;
import com.example.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(path = "/register")
    public DtoUser registerUser(@RequestBody DtoUserIU user) {
        return userService.registerUser(user);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<DtoAuthToken> loginUser(@RequestBody DtoUserLogin request) {
        DtoAuthToken token = authenticationService.loginUser(request.getUsername(), request.getPassword());
        return token != null ? ResponseEntity.ok(token) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exists(@PathVariable Long id) {
        boolean exists = userService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(@RequestHeader("Authorization") String authHeader) {
        boolean success = authenticationService.logout(authHeader);
        return success ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


}
