package com.example.CineVerse.service.impl;

import com.example.CineVerse.entity.Role;
import com.example.CineVerse.entity.User;
import com.example.CineVerse.repository.RoleRepository;
import com.example.CineVerse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class OAuth2UserService extends OidcUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        if (email == null || name == null || email.isEmpty() || name.isEmpty()) {
            throw new OAuth2AuthenticationException("email_not_found");
        }

        User user = userRepository.findByUsernameOrEmail(email, email).orElse(null);

        if (user != null) {
            if (user.getAuthProvider() == AuthProvider.LOCAL) {
                throw new OAuth2AuthenticationException("auth_method_mismatch");
            }
            return oidcUser;
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPublicName(name);
        newUser.setUsername("google_" + UUID.randomUUID().toString().substring(0, 8));
        newUser.setBio("Joined via Google on " + java.time.LocalDate.now());
        newUser.setPassword(null);
        newUser.setRoles(Set.of(userRole));
        newUser.setAuthProvider(AuthProvider.GOOGLE);

        userRepository.save(newUser);
        return oidcUser;
    }
}