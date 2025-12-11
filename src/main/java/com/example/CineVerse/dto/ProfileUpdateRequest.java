package com.example.CineVerse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String publicName;

    @Size(max = 500)
    private String bio;

    @Size(max = 1000)
    private String profilePictureUrl;
}
