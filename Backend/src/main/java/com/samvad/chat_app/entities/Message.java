package com.samvad.chat_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString //for testing while dev
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "recipient_id")
    private Long recipientId;

    @Column(nullable = false, columnDefinition = "TEXT")  //content should be kept in encrypted format with AESKey
    private String content;

    @Column(name = "sent_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sentAt = LocalDateTime.now();

    @Column(name = "is_read", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isRead = false;

    // Constructors
    public Message() {}

    public Message(Long senderId, Long roomId, Long recipientId, String content) {
        this.senderId = senderId;
        this.roomId = roomId;
        this.recipientId = recipientId;
        this.content = content;
    }

}