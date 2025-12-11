package com.example.CineVerse.service;

import com.example.CineVerse.dto.ProfileUpdateRequest;
import com.example.CineVerse.entity.User;

public interface ProfileService {
    User getCurrentUser(String username);
    void updateProfile(String username, ProfileUpdateRequest req);
}
