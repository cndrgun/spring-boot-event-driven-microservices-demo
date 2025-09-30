package com.example.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoUserLogin {
    @NotBlank(message = "Username zorunlu")
    private String username;

    @NotBlank(message = "Password zorunlu")
    private String password;
}
