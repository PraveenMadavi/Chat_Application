package com.eazybyts.chat_app.services;

import com.eazybyts.chat_app.entities.ChatRoom;
import com.eazybyts.chat_app.entities.User;
import com.eazybyts.chat_app.repositories.jpa.ChatRoomRepository;
import com.eazybyts.chat_app.repositories.jpa.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ChatService {
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatService(UserRepository userRepository, ChatRoomRepository chatRoomRepository) {
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public ChatRoom createChatRoom(Long userId, String roomName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ChatRoom newRoom = new ChatRoom();
        newRoom.setName(roomName);
        newRoom.setCreatedAt(Instant.now());

        user.addChatRoom(newRoom);
        return chatRoomRepository.save(newRoom);
    }


    public Set<ChatRoom> getUserChatRooms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getChatRooms();

    }
}