package com.eazybyts.chat_app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Entity
@Table(name = "messages")
@Getter
@Setter
@ToString(exclude = "chatRoom")
//@ToString
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long recipientId;
    private Long senderId;   //turn these into User entity later
    private String content;
    @Column(name = "is_read", nullable = false)
    private boolean read;    //true = read || false = unread
    private Instant time;
    private boolean status;  // true = delivered || false = undelivered
    private String senderName; // User.username

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")  // Explicit column mapping
    private ChatRoom chatRoom;
}
