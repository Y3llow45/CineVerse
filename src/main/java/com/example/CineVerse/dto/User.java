package com.example.CineVerse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String profilePictureUrl;
    private String bio;
}
