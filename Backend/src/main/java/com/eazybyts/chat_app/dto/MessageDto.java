package com.eazybyts.chat_app.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private Long senderId;
    private Long roomId;
    private Long recipientId;
    private String content;
    private Instant sentAt;
    private boolean isRead;
}
