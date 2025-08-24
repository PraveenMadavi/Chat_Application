package com.eazybyts.chat_app.controllers;

import com.eazybyts.chat_app.dto.ChatMessage;
import com.eazybyts.chat_app.dto.MessageRequestDTO;
import com.eazybyts.chat_app.entities.ChatRoom;
import com.eazybyts.chat_app.entities.Message;
import com.eazybyts.chat_app.entities.User;
import com.eazybyts.chat_app.repositories.jpa.ChatRoomRepository;
import com.eazybyts.chat_app.repositories.jpa.MessageRepository;
import com.eazybyts.chat_app.repositories.jpa.UserRepository;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserUtilityController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    MessageRepository messageRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserUtilityController.class);

    @PostMapping("/is-present")
    public ResponseEntity<?> isUserPresent(@RequestBody CheckMail mail) {
        Optional<User> friend = userRepository.findByEmail(mail.getEmail());

        if (friend.isPresent()) {
            logger.info("Email {} is present.", mail.getEmail());
            FriendInfo friendInfo = new FriendInfo();
            friendInfo.setId(friend.get().getId());
            friendInfo.setUsername(friend.get().getUsername());
            System.out.println(friendInfo);
            return ResponseEntity.ok(friendInfo);
        } else {
            logger.warn("Email {} is not present.", mail.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email is not present");
        }
    }

    @PostMapping("/create-chatroom") //
    public ResponseEntity<?> createRoom(@RequestBody RoomInfo roomInfo) {
        logger.info("Trying to create room.");
        // Find creator user
        Optional<User> creatorOptional = userRepository.findById(roomInfo.getCreatorId());
        if (creatorOptional.isEmpty()) {
            logger.error("Chat room creator not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Creator user not found");
        }
        User creator = creatorOptional.get();
        logger.info("creator id:{}", creator.getId());


        // Find friend user
        Optional<User> friendOptional = userRepository.findById(roomInfo.getFriendId());
        if (friendOptional.isEmpty()) {
            logger.error("Chat room friend not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Participant user not found");
        }
        User friend = friendOptional.get();
        logger.info("Room creating with id: {}", friend.getId());

        // Create new chat room
        ChatRoom chatRoom = new ChatRoom();
//        chatRoom.setName(roomInfo.getName());
//        chatRoom.setDescription(roomInfo.getDescription());

        if (roomInfo.isPrivate) {
            chatRoom.setPrivate(roomInfo.isPrivate()); // Assuming you add this field to RoomInfo
        }

        //set creator
        chatRoom.setCreatedBy(creator);
        // Add members
        chatRoom.addMember(creator);
        chatRoom.addMember(friend);

        // Save the chat room
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        logger.info("ChatRoom created and saved successfully");

//        try {
//            //add saved chat room in members list
//            creator.addChatRoom(savedChatRoom);
//            friend.addChatRoom(savedChatRoom);
////            logger.info("Created chatroom is added in both users list of chatroom");
//            //save both users
//            userRepository.save(creator);
//            userRepository.save(friend);
//            logger.info("chatroom added and saved in both users.");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        logger.info("Chat Room Created with id : {}", savedChatRoom.getId());
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setId(savedChatRoom.getId());
        chatRoomDTO.setMemberIds(savedChatRoom.getMembers().stream().map(User::getId).toList());
        System.out.println(chatRoomDTO);
        return ResponseEntity.ok(chatRoomDTO); //Learning : always sent dto, not full entity object to stay away from nesting loops.
    }

    @PostMapping("/messages")
    public ResponseEntity<?> saveMessage(@Valid @RequestBody MessageRequestDTO messageDTO) {
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
        message.setChatRoom(chatRoom);
        message.setRecipientId(messageDTO.getRecipientId());
        message.setSenderId(messageDTO.getSenderId());
        message.setContent(messageDTO.getContent());
        message.setStatus(false); // Default to not delivered
        message.setRead(false);   // Default to unread
        message.setTime(Instant.now());
        message.setSenderName(messageDTO.getSenderName());

        Message savedMessage = messageRepository.save(message);

        // Add message to the chat room
        chatRoom.addMessage(savedMessage);
        chatRoomRepository.save(chatRoom);

        // Convert to response DTO
        MessageRequestDTO responseDTO = getMessageResponseDTO(savedMessage);

        logger.info("Message saved in roomId: {}", messageDTO.getRoomId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/getRooms/{userId}")
    public List<Long> getRooms(@PathVariable Long userId) {
        logger.info("userId requested for roomIds : {}", userId);
        User referenceById = userRepository.getReferenceById(userId);
        List<Long> roomIds = referenceById.getChatRooms().stream().map(ChatRoom::getId).toList();
        System.out.println("Room Ids : " + roomIds);
        return roomIds;
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long roomId) {
        logger.info("Sending messages of room id : {}", roomId);

        try {
            // Check if room exists
            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(roomId);
            if (chatRoomOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ChatRoom chatRoom = chatRoomOpt.get(); //just to check room exist or not.

            // Eagerly fetch messages to avoid LazyInitializationException
            List<Message> messages = messageRepository.findByChatRoomId(roomId);

            // Convert to DTOs
            List<ChatMessage> messageDTOs = messages.stream()
                    .map(ChatMessage::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(messageDTOs);

        } catch (Exception e) {
            logger.error("Error fetching messages for room {}", roomId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rooms/{roomId}/{userId}")
    public ResponseEntity<?> getRoomDetails(@PathVariable Long roomId, @PathVariable Long userId) {
        logger.info("Trying to fetch details roomId: {}, userId: {}", roomId, userId);

        try {
            ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Chat room not found"));

            RoomDetail roomDetail = new RoomDetail();

            // Find the other user (not equal to the provided userId)  //only for private room..... edit later for public rooms after testing
            Optional<User> otherUser = chatRoom.getMembers().stream()
                    .filter(member -> !member.getId().equals(userId))
                    .findFirst();

            if (otherUser.isPresent()) {
                roomDetail.setName(otherUser.get().getUsername());
//                roomDetail.setAvatar(otherUser.get().getProfilePictureUrl());
                roomDetail.setAvatar("https://cdn-icons-png.flaticon.com/512/10903/10903422.png"); //temp

            } else {
                // Fallback if no other user found (shouldn't happen in a proper chat room)
                roomDetail.setName("Unknown User");
                roomDetail.setAvatar("https://cdn-icons-png.flaticon.com/512/10903/10903422.png");
            }

            return ResponseEntity.ok(roomDetail);

        } catch (Exception e) {
            logger.error("Error fetching room details for roomId: {}, userId: {}", roomId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching room details: " + e.getMessage());
        }
    }


    //DTOs
    public static MessageRequestDTO getMessageResponseDTO(Message savedMessage) {
        MessageRequestDTO responseDTO = new MessageRequestDTO();
        responseDTO.setId(savedMessage.getId());
//        responseDTO.setRoomId(savedMessage.getRoomId());
        responseDTO.setRoomId(savedMessage.getChatRoom().getId()); // Learning : only sent room id instead of whole chatroom OB.
        responseDTO.setRecipientId(savedMessage.getRecipientId());
        responseDTO.setSenderId(savedMessage.getSenderId());
        responseDTO.setContent(savedMessage.getContent());
        responseDTO.setStatus(savedMessage.isStatus());
        responseDTO.setRead(savedMessage.isRead());
        responseDTO.setTime(savedMessage.getTime());
        responseDTO.setSenderName(savedMessage.getSenderName());
        return responseDTO;
    }

//    @Data
//    public static class MessageResponseDTO{
//        private Long id;
//        private Long roomId;
//        private Long recipientId;
//        private Long senderId;
//        private String content;
//        private boolean read;    //true = read || false = unread
//        private Instant time;
//        private boolean status;  // true = delivered || false = undelivered

    /// /        private String senderName; // User.username
//    }


    // DTO classes
    @Data
    public static class CheckMail {
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

    @Data
    @ToString
    public static class FriendInfo {
        private Long id;
        private String username;
    }

    @Data
    @ToString
    public static class ChatRoomDTO {
        private Long id;
        private List<Long> memberIds;
    }

    @Data
    public static class RoomDetail {
        private String name;
        private String Avatar;
        //last_message , time, unread, online, typing, lastseen, isGroup, participants,
    }

}
