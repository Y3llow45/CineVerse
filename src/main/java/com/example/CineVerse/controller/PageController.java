package com.example.CineVerse.controller;

import com.example.CineVerse.dto.RegisterRequest;
import com.example.CineVerse.exception.TodoApiException;
import com.example.CineVerse.service.AuthService;
import com.example.CineVerse.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final AuthService authService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerForm(
            @RequestParam String username,
            @RequestParam String publicName,
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        RegisterRequest dto = new RegisterRequest();
        dto.setUsername(username);
        dto.setPublicName(publicName);
        dto.setEmail(email);
        dto.setPassword(password);

        try {
            authService.register(dto);
            return "redirect:/login";
        } catch (TodoApiException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "something went wrong");
            return "register";
        }
    }
}