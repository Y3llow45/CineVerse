package com.example.CineVerse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO { //do i rename this to AuthRegisterDTO?
    private String username;
    private String password;
    private String publicName;
    private String email;
}
