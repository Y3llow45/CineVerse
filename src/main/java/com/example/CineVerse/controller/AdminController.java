package com.example.CineVerse.controller;

import com.example.CineVerse.entity.User;
import com.example.CineVerse.service.impl.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final com.example.CineVerse.repository.UserRepository userRepository;
    private final AuditService auditservice;

    @GetMapping
    public String adminPage(Model model, Authentication auth) {
        auditservice.log(
                auth.getName(),
                "ACCESS_ADMIN_PANEL",
                "self"
        );

        List<User> latest = userRepository.findTop5ByOrderByIdDesc();
        model.addAttribute("latest", latest);
        return "adminPanel";
    }


    @GetMapping("/users")
    @ResponseBody
    public Page<UserSummary> users(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) String q) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<User> p = (q == null || q.isBlank())
                ? userRepository.findAll(pageable)
                : userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPublicNameContainingIgnoreCase(q, q, q, pageable);

        return p.map(UserSummary::from);
    }

    public static record UserSummary(Long id, String username, String email, String publicName) {
        static UserSummary from(User u) {
            return new UserSummary(u.getId(), u.getUsername(), u.getEmail(), u.getPublicName());
        }
    }
}
