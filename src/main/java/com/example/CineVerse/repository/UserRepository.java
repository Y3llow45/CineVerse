package com.example.CineVerse.repository;
import com.example.CineVerse.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Boolean existsByUsername(String username);

    boolean existsByPublicName(@NotBlank String publicName); //not sure about this

    List<User> findTop5ByOrderByIdDesc();

    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPublicNameContainingIgnoreCase(
            String username, String email, String publicName, Pageable pageable);

    List<User> findTop12ByOrderByUpdatedAtDesc();
}
