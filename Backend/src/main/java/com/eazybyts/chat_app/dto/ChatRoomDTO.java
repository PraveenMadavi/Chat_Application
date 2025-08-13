package com.samvaad.chat_app.dto;

import com.samvaad.chat_app.entities.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long id;

    private String name;

    private String description;

    private Long createdBy;

    private String createdByUsername;

    private LocalDateTime createdAt;

    private boolean isPrivate;

    private int memberCount;

    // Static conversion method from Entity to DTO
    public static ChatRoomDTO fromEntity(ChatRoom chatRoom) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .description(chatRoom.getDescription())
                .createdBy(chatRoom.getCreatedBy().getId())
                .createdByUsername(chatRoom.getCreatedBy().getUsername())
                .createdAt(chatRoom.getCreatedAt())
                .isPrivate(chatRoom.isPrivate())
                .build();
    }
}