package com.samvad.chat_app.repositories.jpa;

import com.samvad.chat_app.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories(basePackages = "com.samvad.chat_app.repositories.jpa")
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Find all messages in a chat room
    List<Message> findByRoomIdOrderBySentAtAsc(Long roomId);

    // Find all private messages between two users
    List<Message> findBySenderIdAndRecipientIdOrRecipientIdAndSenderIdOrderBySentAtAsc(
            Long senderId1, Long recipientId1, Long senderId2, Long recipientId2);

    // Find all unread messages for a recipient
    List<Message> findByRecipientIdAndIsReadFalse(Long recipientId);

    // Find all messages sent by a user
    List<Message> findBySenderId(Long senderId);
}