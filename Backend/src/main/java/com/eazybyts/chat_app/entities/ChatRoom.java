package com.eazybyts.chat_app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    private List<User> members = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();


    public void addMessage(Message message) {             //
        messages.add(message);
//        message.setChatRoom(this);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
//        message.setChatRoom(null);
    }

    public void addMember(User user){
        members.add(user);
    }

    public void removeMember(User user){
        members.remove(user);
    }

}
