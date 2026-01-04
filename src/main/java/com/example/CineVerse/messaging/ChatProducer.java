package com.example.CineVerse.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(ChatMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.CHAT_EXCHANGE,
                RabbitConfig.CHAT_ROUTING_KEY,
                message
        );
    }
}
