package com.example.CineVerse.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ChatListener {

    @RabbitListener(queues = RabbitConfig.CHAT_QUEUE)
    public void receive(ChatMessage message) {
        System.out.println(
                "[" + message.from() + " → " + message.to() + "] " + message.content()
        );
    }
}
