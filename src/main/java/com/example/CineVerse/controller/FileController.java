package com.example.CineVerse.controller;

import com.example.CineVerse.entity.User;
import com.example.CineVerse.repository.UserRepository;
import com.example.CineVerse.service.impl.FileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileServiceImpl fileServiceImpl;
    private final UserRepository userRepository;

    @GetMapping
    public String page(Model model, Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        model.addAttribute("files", fileServiceImpl.list(user.getId()));
        return "fileUpload";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Authentication auth) throws Exception {
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        fileServiceImpl.upload(user.getId(), file);
        return "redirect:/files";
    }
}
