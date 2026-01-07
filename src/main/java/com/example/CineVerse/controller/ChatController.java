package com.example.CineVerse.controller;

import com.example.CineVerse.entity.ChatMessage;
import com.example.CineVerse.messaging.ChatProducer;
import com.example.CineVerse.repository.ChatMessageRepository;
import com.example.CineVerse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository repo;
    private final ChatProducer producer;
    private final UserRepository userRepository;

    @GetMapping("/users")
    public List<String> users(@AuthenticationPrincipal UserDetails u) {

        return repo.findChatUsers(u.getUsername());
    }

    @GetMapping("/messages/{with}")
    public List<ChatMessage> messages(@AuthenticationPrincipal UserDetails u,
                                      @PathVariable String with) {
        if (!userRepository.existsByUsername(with)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such user");
        }
        return repo.findConversation(u.getUsername(), with);
    }

    @PostMapping("/send")
    public void send(@AuthenticationPrincipal UserDetails u,
                     @RequestBody ChatMessage msg) {
        msg.setSender(u.getUsername());
        if (!userRepository.existsByUsername(msg.getReceiver())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        producer.send(msg);
    }
}
