package com.example.CineVerse.service;

import com.example.CineVerse.dto.AuthRequestDTO;
import com.example.CineVerse.dto.AuthLoginDTO;

public interface AuthService {
    String register(AuthRequestDTO authRequestDTO);
    String login(AuthLoginDTO authResponseDTO);
}
