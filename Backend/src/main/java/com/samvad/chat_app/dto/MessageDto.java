package com.samvad.chat_app.dto;

import com.samvad.chat_app.entities.Message;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private Long senderId;
    private Long roomId;
    private Long recipientId;
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;


    public static MessageDto fromEntity(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setRoomId(message.getRoomId());
        dto.setRecipientId(message.getRecipientId());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setRead(message.isRead());
        return dto;
    }
}
