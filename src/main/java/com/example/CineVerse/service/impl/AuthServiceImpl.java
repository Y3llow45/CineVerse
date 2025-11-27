package com.example.CineVerse.service.impl;

import com.example.CineVerse.dto.AuthRequestDTO;
import com.example.CineVerse.dto.AuthLoginDTO;
import com.example.CineVerse.entity.Role;
import com.example.CineVerse.entity.User;
import com.example.CineVerse.exception.TodoApiException;
import com.example.CineVerse.repository.UserRepository;
import com.example.CineVerse.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthServiceImpl {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public String register(AuthRequestDTO authRequestDTO) {

        if (authRequestDTO.getPublicName() == null || authRequestDTO.getUsername() == null ||
                authRequestDTO.getEmail() == null || authRequestDTO.getPassword() == null) {
            throw new TodoApiException(HttpStatus.BAD_REQUEST, "All fields are required");
        }
        if(userRepository.existsByUsername(authRequestDTO.getUsername()) || userRepository.existsByEmail(authRequestDTO.getEmail())) {
            throw new TodoApiException(HttpStatus.CONFLICT, "Username or email already exists");
        }

        User user = new User();
        user.setPublicName(authRequestDTO.getPublicName());
        user.setUsername(authRequestDTO.getUsername());
        user.setEmail(authRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(authRequestDTO.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("ROLE_USER");
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        return "User registered successfully";
    }

    @Override
    public String login(AuthLoginDTO authResponseDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                AuthLoginDTO.getUsernameOrEmail(), AuthLoginDTO.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        return token;
    }
}
