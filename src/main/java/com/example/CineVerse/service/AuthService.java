package com.example.CineVerse.service;

import com.example.CineVerse.dto.AuthResponse;
import com.example.CineVerse.dto.RegisterRequest;
import com.example.CineVerse.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

public interface AuthService {
    void register(RegisterRequest dto);
    AuthResponse login(LoginRequest dto);

    Authentication getAuthentication(@Valid LoginRequest dto);
}
