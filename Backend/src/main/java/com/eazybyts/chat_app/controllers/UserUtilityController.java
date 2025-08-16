package com.eazybyts.chat_app.controllers;

import com.eazybyts.chat_app.dto.MessageResponseDTO;
import com.eazybyts.chat_app.entities.ChatRoom;
import com.eazybyts.chat_app.entities.Message;
import com.eazybyts.chat_app.entities.User;
import com.eazybyts.chat_app.repositories.jpa.ChatRoomRepository;
import com.eazybyts.chat_app.repositories.jpa.MessageRepository;
import com.eazybyts.chat_app.repositories.jpa.UserRepository;
import jakarta.validation.Valid;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping ("/api/user")
public class UserUtilityController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    MessageRepository messageRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserUtilityController.class);

    @PostMapping("/is-present")
    public ResponseEntity<Boolean> isUserPresent(@RequestBody CheckMail mail) {
        boolean exists = userRepository.findByEmail(mail.getEmail()).isPresent();

        if (exists) {
            logger.info("Email {} is present.", mail.getEmail());
            return ResponseEntity.ok(true);
        } else {
            logger.warn("Email {} is not present.", mail.getEmail());
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping("/create-chatroom") //
    public ResponseEntity<?> createRoom(@RequestBody RoomInfo roomInfo) {
        // Find creator user
        Optional<User> creatorOptional = userRepository.findById(roomInfo.getCreatorId());
        if (creatorOptional.isEmpty()) {
            logger.error("Chat room creator not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Creator user not found");
        }
        User creator = creatorOptional.get();

        // Find participant user
        Optional<User> friendOptional = userRepository.findById(roomInfo.getFriendId());
        if (friendOptional.isEmpty()) {
            logger.error("Chat room participant not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Participant user not found");
        }
        User participant = friendOptional.get();

        // Create new chat room
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(roomInfo.getName());
        chatRoom.setDescription(roomInfo.getDescription());
        chatRoom.setCreatedBy(creator);
        chatRoom.setPrivate(roomInfo.isPrivate()); // Assuming you add this field to RoomInfo

        // Add members
        chatRoom.addMember(creator);
        chatRoom.addMember(participant);

        // Save the chat room
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        logger.info("Chat Room Created.");
        return ResponseEntity.ok(savedChatRoom);
    }

    @PostMapping("/messages")
    public ResponseEntity<?> saveMessage(@Valid @RequestBody MessageResponseDTO messageDTO) {
        // Validate required fields
        if (messageDTO.getRoomId() == null) {
            return ResponseEntity.badRequest().body("Room ID is required");
        }
        if (messageDTO.getSenderId() == null) {
            return ResponseEntity.badRequest().body("Sender ID is required");
        }
        if (messageDTO.getRecipientId() == null) {
            return ResponseEntity.badRequest().body("Recipient ID is required");
        }
        if (messageDTO.getContent() == null || messageDTO.getContent().isBlank()) {
            return ResponseEntity.badRequest().body("Message content cannot be empty");
        }

        // Find the chat room
        Optional<ChatRoom> roomOptional = chatRoomRepository.findById(messageDTO.getRoomId());
        if (roomOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found to save message.");
        }
        ChatRoom chatRoom = roomOptional.get();

        // Create and save the message
        Message message = new Message();
        message.setRoomId(messageDTO.getRoomId());
        message.setRecipientId(messageDTO.getRecipientId());
        message.setSenderId(messageDTO.getSenderId());
        message.setContent(messageDTO.getContent());
        message.setStatus(false); // Default to not delivered
        message.setRead(false);   // Default to unread
        message.setTime(Instant.now());
        message.setSenderName(messageDTO.getSenderName());
        message.setChatRoom(chatRoom);

        Message savedMessage = messageRepository.save(message);

        // Add message to the chat room
        chatRoom.addMessage(savedMessage);
        chatRoomRepository.save(chatRoom);

        // Convert to response DTO
        MessageResponseDTO responseDTO = getMessageResponseDTO(savedMessage);

        logger.info("Message saved in roomId: {}", messageDTO.getRoomId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    private static MessageResponseDTO getMessageResponseDTO(Message savedMessage) {
        MessageResponseDTO responseDTO = new MessageResponseDTO();
        responseDTO.setId(savedMessage.getId());
        responseDTO.setRoomId(savedMessage.getRoomId());
        responseDTO.setRecipientId(savedMessage.getRecipientId());
        responseDTO.setSenderId(savedMessage.getSenderId());
        responseDTO.setContent(savedMessage.getContent());
        responseDTO.setStatus(savedMessage.isStatus());
        responseDTO.setRead(savedMessage.isRead());
        responseDTO.setTime(savedMessage.getTime());
        responseDTO.setSenderName(savedMessage.getSenderName());
        return responseDTO;
    }


    // DTO classes
    @Data
    public static class CheckMail{
        private String email; //recipient email
    }

    @Data
    public static class RoomInfo {
        private Long creatorId;
        private Long friendId;
        private String name;
        private String description;
        private boolean isPrivate; // Added this field
    }

}
