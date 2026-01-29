package com.example.CineVerse.handlers;

import com.example.CineVerse.entity.User;
import com.example.CineVerse.repository.UserRepository;
import com.example.CineVerse.security.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OAuth2AuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email = ((OidcUser) authentication.getPrincipal()).getEmail();
        User user = userRepository.findByUsernameOrEmail(email, email)
                .orElseThrow(() -> new IllegalStateException("user not found after oauth"));

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .toList();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtTokenProvider.generateToken(auth);

        Cookie cookie = new Cookie("AUTH_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) 86400);
        response.addHeader("Set-Cookie",
                String.format("AUTH_TOKEN=%s; Path=/; HttpOnly; Max-Age=%d; SameSite=Lax%s",
                        token,
                        86400,
                        cookie.getSecure() ? "; Secure" : "")
        );

        response.sendRedirect("/home");
    }
}
