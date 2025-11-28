package com.example.CineVerse.service;

import com.example.CineVerse.dto.AuthRegisterDTO;
import com.example.CineVerse.dto.AuthLoginDTO;

public interface AuthService {
    String register(AuthRegisterDTO authRegisterDTO);
    String login(AuthLoginDTO authResponseDTO);
}
