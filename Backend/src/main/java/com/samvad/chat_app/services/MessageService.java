package com.samvad.chat_app.services;

import com.samvad.chat_app.entities.Message;
import com.samvad.chat_app.repositories.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // Send a message to a room or private recipient     .............and to save also......still in dev
    @Transactional
    public Message sendMessage(Long senderId, Long roomId, Long recipientId, String content) {
        if ((roomId == null && recipientId == null) || (roomId != null && recipientId != null)) {
            throw new IllegalArgumentException("Message must be either for a room or a private recipient");
        }

        Message message = new Message();
        message.setSenderId(senderId);
        message.setRoomId(roomId);
        message.setRecipientId(recipientId);
        message.setContent(content);

        return messageRepository.save(message);
    }

    // Get messages for a chat room
    public List<Message> getRoomMessages(Long roomId) {
        return messageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    // Get private messages between two users
    public List<Message> getPrivateMessages(Long user1Id, Long user2Id) {
        return messageRepository.findBySenderIdAndRecipientIdOrRecipientIdAndSenderIdOrderBySentAtAsc(
                user1Id, user2Id, user1Id, user2Id);
    }

    // Mark messages as read   ...list of messages
    @Transactional
    public void markMessagesAsRead(List<Long> messageIds) {
        messageRepository.findAllById(messageIds).forEach(message -> {
            message.setRead(true);
            messageRepository.save(message);
        });
    }

    // Get unread messages for a user
    public List<Message> getUnreadMessages(Long userId) {
        return messageRepository.findByRecipientIdAndIsReadFalse(userId);
    }
}
