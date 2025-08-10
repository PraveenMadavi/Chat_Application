    package com.samvad.chat_app.repositories.jpa;

    import com.samvad.chat_app.entities.User;
    import jakarta.transaction.Transactional;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Modifying;
    import org.springframework.data.jpa.repository.Query;
//    import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;

    import java.util.List;
    import java.util.Optional;

    @Repository
//    @EnableJpaRepositories(basePackages = "com.samvad.chat_app.repositories.jpa")
    public interface UserRepository extends JpaRepository<User, Long> {

        // Find by email (unique constraint ensures only one result)
        Optional<User> findByEmail(String email);

        // Check if email exists
        boolean existsByEmail(String email);

        // Find by username (not unique in this version)
        Optional<User> findByUsername(String username);

        // Custom update for last seen timestamp
        @Transactional
        @Modifying(clearAutomatically = true)
        @Query("UPDATE User u SET u.lastSeenAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
        void updateLastSeen(@Param("userId") Long userId);

        // Custom update for user status
        @Transactional
        @Modifying(clearAutomatically = true)
        @Query("UPDATE User u SET u.status = :status WHERE u.id = :userId")
        void updateStatus(@Param("userId") Long userId, @Param("status") User.Status status);

    //    // Custom update for failed login attempts
    //    @Modifying
    //    @Query("UPDATE User u SET u.failedLoginAttempts = :attempts WHERE u.id = :userId")
    //    void updateFailedLoginAttempts(@Param("userId") Long userId, @Param("attempts") int attempts);

        // Custom update for account lock status
    //    @Modifying
    //    @Query("UPDATE User u SET u.accountNonLocked = :nonLocked WHERE u.id = :userId")
    //    void updateAccountLockStatus(@Param("userId") Long userId, @Param("nonLocked") boolean nonLocked);

        // Find all active users
        @Query("SELECT u FROM User u WHERE u.isActive = true")
        List<User> findAllActiveUsers();

    //    // Find users by role
    //    List<User> findByRole(User.Role role);

        // Find users by status
        List<User> findByStatus(User.Status status);

    //    List<User> findByRoleAndIsActive(User.Role role, boolean b);
    }
