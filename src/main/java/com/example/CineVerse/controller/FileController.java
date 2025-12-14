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

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileServiceImpl fileServiceImpl;
    private final UserRepository userRepository;

    @GetMapping
    public String page(Model model,
                       Authentication auth,
                       @RequestParam(required = false) String error,
                       @RequestParam(required = false) String success) {

        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        model.addAttribute("files", fileServiceImpl.list(user.getId()));

        if (error != null) {
            model.addAttribute("error", URLDecoder.decode(error, StandardCharsets.UTF_8));
        }
        if (success != null) {
            model.addAttribute("success", "Upload successful");
        }

        return "fileUpload";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        try {
            fileServiceImpl.upload(user.getId(), file);
            return "redirect:/files?success=true";
        } catch (IllegalStateException | IllegalArgumentException e) {
            String msg = URLEncoder.encode(e.getMessage() == null ? "error" : e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/files?error=" + msg;
        } catch (IOException e) {
            String msg = URLEncoder.encode("upload failed", StandardCharsets.UTF_8);
            return "redirect:/files?error=" + msg;
        }
    }
}