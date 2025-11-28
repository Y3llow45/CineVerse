package com.example.CineVerse.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRegisterDTO {
    @NotBlank
    private String username;
    @NotBlank private String publicName;
    @NotBlank
    @Email
    private String email;
    @NotBlank private String password;
}
