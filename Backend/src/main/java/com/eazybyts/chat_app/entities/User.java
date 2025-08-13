package com.samvaad.chat_app.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    private String status = "OFFLINE";

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "is_email_verified")
    private boolean isEmailVerified = false;

    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true; //.....................required or not

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "ENUM('USER', 'ADMIN', 'MODERATOR') DEFAULT 'USER'")
    private Role role = Role.USER;

    public enum Role {
        USER, ADMIN, MODERATOR
    }

    public enum Status {
        ONLINE, OFFLINE, AWAY, BUSY
    }

    // One-to-Many relationship with ChatRoom
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms = new ArrayList<>();


    public void addChatRoom(ChatRoom chatRoom) {
        chatRooms.add(chatRoom);
        chatRoom.setCreatedBy(this);
    }

    public void removeChatRoom(ChatRoom chatRoom) {
        chatRooms.remove(chatRoom);
        chatRoom.setCreatedBy(null);
    }
}