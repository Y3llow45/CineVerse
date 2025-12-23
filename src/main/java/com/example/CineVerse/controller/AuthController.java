package com.example.CineVerse.controller;

import com.example.CineVerse.dto.AuthResponse;
import com.example.CineVerse.dto.LoginRequest;
import com.example.CineVerse.dto.RegisterRequest;
import com.example.CineVerse.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest dto) {
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest dto,
                                              HttpServletRequest request) {
        AuthResponse res = authService.login(dto);

        Authentication authentication = authService.getAuthentication(dto);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        return ResponseEntity.ok(res);
    }

    @PostMapping("/2fa/setup")
    public ResponseEntity<?> totpSetup(@Valid @RequestBody LoginRequest dto) {
        String otpAuthUrl = authService.generateTotpSetup(dto.getUsernameOrEmail());
        return ResponseEntity.ok().body(java.util.Map.of("otpAuthUrl", otpAuthUrl));
    }

    @PostMapping("/2fa/confirm")
    public ResponseEntity<?> totpConfirm(@RequestBody java.util.Map<String,String> body) {
        String username = body.get("username");
        String code = body.get("code");
        boolean ok = authService.confirmTotpSetup(username, code);
        if (!ok) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<?> totpDisable(@RequestBody java.util.Map<String,String> body) {
        String username = body.get("username");
        authService.disableTotpForUser(username);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return "redirect:/home";
    }
}
