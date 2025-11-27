package com.example.CineVerse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    private String username;
    private String password;
    private String publicName;
    private String email;
}
