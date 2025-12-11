package com.example.CineVerse.controller;

import com.example.CineVerse.dto.ProfileUpdateRequest;
import com.example.CineVerse.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/profile")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public String profileForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        var user = profileService.getCurrentUser(principal.getUsername());
        var dto = new ProfileUpdateRequest();
        dto.setPublicName(user.getPublicName());
        dto.setBio(user.getBio());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        model.addAttribute("profile", dto);
        return "profile";
    }

    @PostMapping
    public String updateProfile(@AuthenticationPrincipal UserDetails principal,
                                @Valid @ModelAttribute("profile") ProfileUpdateRequest profile,
                                BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("error", "Validation failed");
            return "profile";
        }

        try {
            profileService.updateProfile(principal.getUsername(), profile);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            return "profile";
        }

        model.addAttribute("success", "Profile updated");
        return "profile";
    }
}