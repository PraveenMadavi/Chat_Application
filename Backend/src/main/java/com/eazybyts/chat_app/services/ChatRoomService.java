package com.samvaad.chat_app.services;

import com.samvaad.chat_app.dto.ChatRoomDTO;
import com.samvaad.chat_app.entities.ChatRoom;
import com.samvaad.chat_app.entities.User;
import com.samvaad.chat_app.repositories.jpa.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository, UserService userService) {
        this.chatRoomRepository = chatRoomRepository;
        this.userService = userService;
    }

    public ChatRoom createChatRoom(ChatRoomDTO chatRoomDTO, Long userId) {
        User creator = userService.getUserById(userId);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(chatRoomDTO.getName());
        chatRoom.setDescription(chatRoomDTO.getDescription());
        chatRoom.setCreatedBy(creator);
        chatRoom.setPrivate(chatRoomDTO.isPrivate());

        return chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoom> getAvailableChatRooms(Long userId) {
        User user = userService.getUserById(userId);
        return chatRoomRepository.findByIsPrivateFalseOrCreatedBy(user);
    }

    // Other service methods
}
