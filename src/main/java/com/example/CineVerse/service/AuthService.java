package com.example.CineVerse.service;

import com.example.CineVerse.dto.AuthRequestDTO;
import com.example.CineVerse.dto.AuthResponseDTO;

public interface AuthService {
    String register(AuthRequestDTO authRequestDTO);
    String login(AuthResponseDTO authResponseDTO);
}
