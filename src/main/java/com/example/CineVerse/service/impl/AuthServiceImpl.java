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
import com.example.CineVerse.security.TotpUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.spi.DefaultLoggingEventBuilder;
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
    private final AuditService auditService;

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

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
        auditService.log("REGISTER", dto.getUsername(), "self");
        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsernameOrEmail(), dto.getPassword())
        );
        User user = userRepository.findByUsernameOrEmail(dto.getUsernameOrEmail(), dto.getUsernameOrEmail())
                .orElseThrow(() -> new TodoApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (user.isTotpEnabled()) {
            if (dto.getTotpCode() == null || dto.getTotpCode().isBlank()) {
                throw new TodoApiException(HttpStatus.UNAUTHORIZED, "TOTP_REQUIRED");
            }
            if (!TotpUtil.verifyCode(user.getTotpSecret(), dto.getTotpCode())) {
                throw new TodoApiException(HttpStatus.UNAUTHORIZED, "Invalid TOTP code");
            }
        }

        String token = tokenProvider.generateToken(authentication);
        Date expires = tokenProvider.getExpiryFromToken(token);
        auditService.log("LOGIN", dto.getUsernameOrEmail(), "self");
        return new AuthResponse(token, "Bearer", expires);
    }

    @Override
    public String generateTotpSetup(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new TodoApiException(HttpStatus.NOT_FOUND, "User not found"));
        String secret = TotpUtil.generateSecret();
        user.setTotpSecret(secret);
        user.setTotpEnabled(false);
        userRepository.save(user);
        return TotpUtil.getOtpAuthUrl("CineVerse", user.getUsername(), secret);
    }

    @Override
    public boolean confirmTotpSetup(String usernameOrEmail, String code) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new TodoApiException(HttpStatus.NOT_FOUND, "User not found"));
        String secret = user.getTotpSecret();
        if (secret == null) throw new TodoApiException(HttpStatus.BAD_REQUEST, "No pending TOTP setup");
        boolean ok = TotpUtil.verifyCode(secret, code);
        if (ok) {
            user.setTotpEnabled(true);
            userRepository.save(user);
            auditService.log("ENABLE_TOTP", usernameOrEmail, "self");
        }
        return ok;
    }

    @Override
    public void disableTotpForUser(String usernameOrEmail) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new TodoApiException(HttpStatus.NOT_FOUND, "User not found"));
        user.setTotpEnabled(false);
        user.setTotpSecret(null);
        userRepository.save(user);
        auditService.log("DISABLE_TOTP", usernameOrEmail, "self");
    }


    public Authentication getAuthentication(LoginRequest dto) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsernameOrEmail(), dto.getPassword())
        );
    }
}
