package com.example.CineVerse.repository;

import com.example.CineVerse.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
      select distinct
      case when m.sender = :me then m.receiver else m.sender end
      from ChatMessage m
      where m.sender = :me or m.receiver = :me
    """)
    List<String> findChatUsers(String me);

    @Query("""
      from ChatMessage m
      where (m.sender = :me and m.receiver = :other)
         or (m.sender = :other and m.receiver = :me)
      order by m.createdAt
    """)
    List<ChatMessage> findConversation(String me, String other);
}