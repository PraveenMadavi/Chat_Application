package com.samvad.chat_app.repositories.jpa;

import com.samvad.chat_app.entities.ChatRoom;
import com.samvad.chat_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories(basePackages = "com.samvad.chat_app.repositories.jpa")
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByIsPrivateFalse();
    List<ChatRoom> findByCreatedBy(User user);
    List<ChatRoom> findByIsPrivateFalseOrCreatedBy(User user);

    // findByRoomId(roomId);  already provided <--- it returns optional ChatRoom
}
