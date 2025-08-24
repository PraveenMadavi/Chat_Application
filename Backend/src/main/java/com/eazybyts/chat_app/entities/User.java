package com.eazybyts.chat_app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude ="chatRooms" )
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
    private Instant lastSeenAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

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

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "chat_room_members", // This should match the join table name in ChatRoom
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "chat_room_id")
//    )
//    private Set<ChatRoom> chatRooms = new HashSet<>();

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
    private Set<ChatRoom> chatRooms = new HashSet<>();


    public void addChatRoom(ChatRoom chatRoom) {
        this.chatRooms.add(chatRoom);
        chatRoom.getMembers().add(this);
    }

    public void removeChatRoom(ChatRoom chatRoom) {
        this.chatRooms.remove(chatRoom);
        chatRoom.getMembers().remove(this);
    }

}