package com.samvaad.chat_app.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roomId;
    private Long recipientId;
    private Long senderId;
    private String content;
    @Column(name = "is_read", nullable = false)
    private boolean read;  //true = read || false = unread
    private Timestamp time;
    private boolean status;  // true = delivered || false = undelivered
    private String senderName; // User.username

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
}
