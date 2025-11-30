package com.example.CineVerse.service.impl;

import com.example.CineVerse.dto.AuthResponse;
import com.example.CineVerse.dto.RegisterRequest;
import com.example.CineVerse.dto.LoginRequest;
import com.example.CineVerse.entity.Role;
import com.example.CineVerse.entity.User;
import com.example.CineVerse.exception.TodoApiException;
import com.example.CineVerse.repository.RoleRepository;
import com.example.CineVerse.repository.UserRepository;
import com.example.CineVerse.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements com.example.CineVerse.service.AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    private final Pattern strongPassword = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$"
    );

    @Override
    public void register(RegisterRequest dto) {
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new TodoApiException(HttpStatus.CONFLICT, "Username already exists");
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new TodoApiException(HttpStatus.CONFLICT, "Email already exists");
        if (userRepository.existsByPublicName(dto.getPublicName()))
            throw new TodoApiException(HttpStatus.CONFLICT, "Public name already exists");
        if (!strongPassword.matcher(dto.getPassword()).matches())
            throw new TodoApiException(HttpStatus.BAD_REQUEST, "Password too weak (min 8, upper, lower, digit, special)");

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPublicName(dto.getPublicName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // assign ROLE_USER
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsernameOrEmail(), dto.getPassword())
        );
        String token = tokenProvider.generateToken(authentication);
        Date expires = tokenProvider.getExpiryFromToken(token);
        return new AuthResponse(token, "Bearer", expires);
    }
}
