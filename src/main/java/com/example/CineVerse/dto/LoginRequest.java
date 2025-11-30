package com.example.CineVerse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    public String usernameOrEmail;
    private String password;
}
