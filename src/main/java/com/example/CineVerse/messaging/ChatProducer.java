package com.example.CineVerse.messaging;

import com.example.CineVerse.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(ChatMessage msg) {
        rabbitTemplate.convertAndSend(RabbitConfig.CHAT_QUEUE, msg);
    }
}

