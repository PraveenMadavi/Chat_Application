package com.eazybyts.chat_app.controllers;

import com.eazybyts.chat_app.dto.ChatMessage;
import com.eazybyts.chat_app.entities.ChatRoom;
import com.eazybyts.chat_app.entities.Message;
import com.eazybyts.chat_app.repositories.jpa.ChatRoomRepository;
import com.eazybyts.chat_app.repositories.jpa.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Controller
public class ChatController {

    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    MessageRepository messageRepository;

    @MessageMapping("/chat.room.{roomId}")
    @SendTo("/topic/room.{roomId}") //destination url
    public ChatMessage sendMessage(@DestinationVariable Long roomId,
                                   ChatMessage chatMessage) {
        System.out.println("Received message for room: " + roomId);

        // Check if room exists
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
        if (chatRoomOpt.isEmpty()) {
            System.err.println("Chat room not found: " + roomId);
            // You might want to throw an exception or return an error message
            return createErrorMessage("Room not found: " + roomId);
        }

        ChatRoom chatRoom = chatRoomOpt.get();

        // Save message to database
        Message message = new Message();
        message.setContent(chatMessage.getText()); // Use getter
        message.setChatRoom(chatRoom);
        message.setSenderId(chatMessage.getSenderId());
        message.setSenderName(chatMessage.getSenderName());
        message.setTime(Instant.now());
        message.setStatus(true);

        Message savedMessage = messageRepository.save(message);

        // Convert savedMessage back to ChatMessage DTO
        ChatMessage responseMessage = getMessage(roomId, chatMessage, savedMessage);

        System.out.println("Message saved and broadcast: " + responseMessage.getId());
        return responseMessage;
    }

    private static ChatMessage getMessage(Long roomId, ChatMessage chatMessage, Message savedMessage) {
        ChatMessage responseMessage = new ChatMessage();
        responseMessage.setId(savedMessage.getId());
        responseMessage.setText(savedMessage.getContent());
        responseMessage.setRoomId(roomId);
        responseMessage.setSenderId(chatMessage.getSenderId());
        responseMessage.setSenderName(chatMessage.getSenderName());
        responseMessage.setSenderAvatar(chatMessage.getSenderAvatar());
        responseMessage.setTimestamp(savedMessage.getTime().toString());
        responseMessage.setType(chatMessage.getType());
        responseMessage.setStatus("sent"); // Changed to DELIVERED since it's being broadcast
        return responseMessage;
    }

    private static ChatMessage getChatMessage(Long roomId, ChatMessage chatMessage, Message savedMessage) {
        return getMessage(roomId, chatMessage, savedMessage);
    }

    @MessageMapping("/chat.addUser")
    @SendToUser("/queue/messages")
    public ChatMessage addUser(ChatMessage message,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add user to session
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", message.getSenderName());
        return message;
    }


    //DTO
//    @Data
//    public static class ChatMessage {
//        private Long id;
//        private String text;
//        private Long roomId;
//        private Long senderId;
//        private String senderName;
//        private String senderAvatar;
//        private String timestamp;
//        private String type; // CHAT, JOIN, LEAVE
//        private String status; // SENT, DELIVERED, READ
//    }

    private ChatMessage createErrorMessage(String errorMessage) {
        ChatMessage error = new ChatMessage();
        error.setType("ERROR");
        error.setText(errorMessage);
        error.setTimestamp(Instant.now().toString());
        return error;
    }
}
