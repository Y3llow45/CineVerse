package com.example.CineVerse.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String code = "oauth_error";
        if (exception instanceof OAuth2AuthenticationException) {
            String msg = ((OAuth2AuthenticationException) exception).getError().getErrorCode();
            if ("auth_method_mismatch".equals(msg)) code = "AUTH_METHOD_MISMATCH";
            if ("email_not_found".equals(msg)) code = "EMAIL_NOT_FOUND";
        }
        response.sendRedirect("/login?error=" + code);
    }
}