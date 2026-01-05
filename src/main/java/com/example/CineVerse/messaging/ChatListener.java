package com.example.CineVerse.messaging;

import com.example.CineVerse.entity.ChatMessage;
import com.example.CineVerse.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatListener {

    private final ChatMessageRepository repo;

    @RabbitListener(queues = RabbitConfig.CHAT_QUEUE)
    public void receive(ChatMessage msg) {
        repo.save(msg);
    }
}

