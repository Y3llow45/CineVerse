package com.example.CineVerse.controller;

import com.example.CineVerse.dto.AuthRegisterDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRegisterDTO authRegisterDTO) {
        String response = authService.register(authRegisterDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
