package com.samvaad.chat_app.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "participants")
@Data
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String avatar;
    private Boolean isOnline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
