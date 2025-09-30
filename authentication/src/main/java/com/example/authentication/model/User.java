package com.example.authentication.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "İsim Boş Olamaz")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Soyisim Boş Olamaz")
    private String surname;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Şifre Boş Olamaz")
    private String password;

    @Column(nullable = true, unique = true)
    @Email(message = "Lütfen geçerli bir e-mail adresi giriniz.")
    private String email;

    private String phone;
}
