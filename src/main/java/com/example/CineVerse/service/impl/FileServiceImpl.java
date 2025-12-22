package com.example.CineVerse.service.impl;

import com.example.CineVerse.entity.User;
import com.example.CineVerse.entity.UserFile;
import com.example.CineVerse.repository.UserFileRepository;
import com.example.CineVerse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl {

    private final UserFileRepository repo;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public void upload(Long userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("empty file");
        if (file.getSize() > 5_000_000L) throw new IllegalArgumentException("file too big (max 5MB)");

        long count = repo.countByUserId(userId);
        if (count >= 5) throw new IllegalStateException("max 5 files per user");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        UserFile uf = new UserFile();
        uf.setUser(user);
        uf.setFileName(file.getOriginalFilename());
        uf.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        uf.setData(file.getBytes());
        uf.setSize(file.getSize());
        auditService.log("UPLOAD_FILE", user.getUsername(), uf.getFileName());
        repo.save(uf);
    }

    public List<UserFile> list(Long userId) {
        return repo.findByUserId(userId);
    }

    public void delete(Long fileId, Long userId) {
        UserFile file = repo.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("file not found"));

        if (!file.getUser().getId().equals(userId)) {
            throw new IllegalStateException("not your file");
        }
        auditService.log("DELETE_FILE", file.getUser().getUsername(), file.getFileName());
        repo.delete(file);
    }

}
