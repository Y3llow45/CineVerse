package com.example.CineVerse.service.impl;

import com.example.CineVerse.entity.AuditLog;
import com.example.CineVerse.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repo;

    public void log(String action, String actor, String target) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setActor(actor);
        log.setTarget(target);
        repo.save(log);
    }

}

