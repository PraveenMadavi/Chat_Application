package com.eazybyts.chat_app.dto;

import lombok.Data;

import java.time.Instant;

@Data

public class MessageResponseDTO {
    private Long id;
    private Long roomId;
    private Long recipientId;
    private Long senderId;
    private String content;
    private boolean read;
    private Instant time;
    private boolean status;
    private String senderName;
   // private String formattedTime; // Optional: formatted timestamp for UI
    private String roomName;
   // private String senderAvatar; // URL to sender's avatar
}
