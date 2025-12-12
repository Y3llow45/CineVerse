package com.example.CineVerse.repository;

import com.example.CineVerse.entity.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFileRepository extends JpaRepository<UserFile, Long> {
    List<UserFile> findByUserId(Long userId);
    long countByUserId(Long userId);
}