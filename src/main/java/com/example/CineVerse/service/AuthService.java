package com.example.CineVerse.service;

import com.example.CineVerse.dto.AuthResponse;
import com.example.CineVerse.dto.RegisterRequest;
import com.example.CineVerse.dto.LoginRequest;

public interface AuthService {
    void register(RegisterRequest dto);
    AuthResponse login(LoginRequest dto);
}
