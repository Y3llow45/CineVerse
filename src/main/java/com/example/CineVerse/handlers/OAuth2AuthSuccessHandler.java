package com.example.CineVerse.handlers;

import com.example.CineVerse.entity.User;
import com.example.CineVerse.repository.UserRepository;
import com.example.CineVerse.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.Authentication authentication) throws IOException {
        OidcUser oidc = (OidcUser) authentication.getPrincipal();
        String email = oidc.getEmail();
        if (email == null) throw new IllegalStateException("email not found after oauth");

        User user = userRepository.findByUsernameOrEmail(email, email)
                .orElseThrow(() -> new IllegalStateException("user not found after oauth"));

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .toList();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        String token = jwtTokenProvider.generateToken(auth);
        int maxAge = (int) (jwtTokenProvider.getExpiryFromToken(token).getTime() - System.currentTimeMillis()) / 1000;
        if (maxAge < 0) maxAge = 86400;

        boolean secure = request.isSecure();
        String cookie = String.format("AUTH_TOKEN=%s; Path=/; HttpOnly; Max-Age=%d; SameSite=Lax%s",
                token, maxAge, secure ? "; Secure" : "");

        response.addHeader("Set-Cookie", cookie);
        response.sendRedirect("/home");
    }
}
