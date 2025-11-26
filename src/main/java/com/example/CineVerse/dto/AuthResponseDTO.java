package com.example.CineVerse.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class AuthResponseDTO {
    private String token;
    private Date expiresAt;
}
