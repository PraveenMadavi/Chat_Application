package com.samvad.chat_app.repositories;

import com.samvad.chat_app.entities.ChatRoom;
import com.samvad.chat_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByIsPrivateFalse();
    List<ChatRoom> findByCreatedBy(User user);
    List<ChatRoom> findByIsPrivateFalseOrCreatedBy(User user);

    // findByRoomId(roomId);  already provided <--- it returns optional ChatRoom
}
