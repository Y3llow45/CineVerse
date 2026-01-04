package com.example.CineVerse.messaging;

public record ChatMessage(
        String from,
        String to,
        String content,
        long timestamp
) {}
