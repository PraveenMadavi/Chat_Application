package com.samvad.chat_app.services;

import com.samvad.chat_app.entities.User;
import com.samvad.chat_app.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor//........does this require
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.getReferenceById(userId);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void updateUserStatus(Long userId, User.Status status) {
        userRepository.updateStatus(userId, status);
    }

    @Transactional
    public void recordUserLogin(Long userId) {
        userRepository.updateLastSeen(userId);
        userRepository.updateStatus(userId, User.Status.ONLINE);
    }

    @Transactional
    public void updateLastSeen(Long userId) {
        userRepository.updateLastSeen(userId);
    }

//    @Transactional
//    public void recordFailedLoginAttempt(Long userId) {
//        userRepository.updateFailedLoginAttempts(userId,
//                userRepository.findById(userId)
//                        .map(user -> (Integer) (user.getFailedLoginAttempts() + 1))
//                        .orElse(1)
//        );
//    }

//    public List<User> getAllActiveAdmins() {
//        return userRepository.findByRoleAndIsActive(User.Role.ADMIN, true);
//    }
}