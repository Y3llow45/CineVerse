package com.example.CineVerse.service.impl;

import com.example.CineVerse.dto.ProfileUpdateRequest;
import com.example.CineVerse.entity.User;
import com.example.CineVerse.exception.TodoApiException;
import com.example.CineVerse.repository.UserRepository;
import com.example.CineVerse.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new TodoApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    @Transactional
    public void updateProfile(String username, ProfileUpdateRequest req) {
        User user = getCurrentUser(username);

        if (!req.getPublicName().equals(user.getPublicName()) &&
                userRepository.existsByPublicName(req.getPublicName())) {
            throw new TodoApiException(HttpStatus.CONFLICT, "Public name already taken");
        }

        user.setPublicName(req.getPublicName());
        user.setBio(req.getBio());
        user.setProfilePictureUrl(req.getProfilePictureUrl());

        userRepository.save(user);
    }
}