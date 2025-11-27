package com.example.CineVerse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginDTO {
    private String token;
    private Date expiresAt;
    public String usernameOrEmail;
    private String password;
}
