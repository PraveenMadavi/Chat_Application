package com.eazybyts.chat_app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"createdBy","members", "messages"})
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Many-to-One relationship with User
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @Column(updatable = false)
    @CreationTimestamp
    private Instant createdAt = Instant.now();

    private boolean Private = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "chat_room_members",
            joinColumns = @JoinColumn(name = "chat_room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();


    public void addMessage(Message message) {             //
        message.setChatRoom(this);
        messages.add(message);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setChatRoom(null);
    }

    public void addMember(User user) {
        this.members.add(user);
        user.getChatRooms().add(this);
    }

    public void removeMember(User user) {
        this.members.remove(user);
        user.getChatRooms().remove(this);
    }

}
