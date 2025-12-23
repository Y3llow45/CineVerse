package com.example.CineVerse.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class LoginRequest {
    public String usernameOrEmail;
    private String password;
    private String totpCode;
}
