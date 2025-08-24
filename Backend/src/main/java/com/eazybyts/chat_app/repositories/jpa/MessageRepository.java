package com.eazybyts.chat_app.repositories.jpa;

import com.eazybyts.chat_app.entities.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Paginated messages for a room
    @Query("SELECT m FROM Message m WHERE m.chatRoom.id = :roomId ORDER BY m.time DESC")
    Page<Message> findByRoomId(@Param("roomId") Long roomId, Pageable pageable);

//    // Count messages in a room
//    long countByChatRoom_Id(Long roomId);

    // Add this method to fetch messages by room ID
    @Query("SELECT m FROM Message m WHERE m.chatRoom.id = :roomId ORDER BY m.time ASC")
    List<Message> findByChatRoomId(@Param("roomId") Long roomId);

    // Or use this if you want to join fetch to avoid N+1 queries
    @Query("SELECT m FROM Message m JOIN FETCH m.chatRoom WHERE m.chatRoom.id = :roomId ORDER BY m.time ASC")
    List<Message> findByChatRoomIdWithFetch(@Param("roomId") Long roomId);

//    // Count unread messages in a room
//    long countByChatRoom_IdAndReadFalse(Long roomId);

    // Get unread messages (non-paginated)
    @Query("SELECT m FROM Message m WHERE m.chatRoom.id = :roomId AND m.read = false ORDER BY m.time DESC")
    List<Message> findUnreadByRoomId(@Param("roomId") Long roomId);

    // Get paginated unread messages
    @Query("SELECT m FROM Message m WHERE m.chatRoom.id = :roomId AND m.read = false ORDER BY m.time DESC")
    Page<Message> findUnreadByRoomIdWithPagination(@Param("roomId") Long roomId, Pageable pageable);

    // Mark messages as read
    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.id IN :messageIds")
    void markMessagesAsRead(@Param("messageIds") List<Long> messageIds);
}
