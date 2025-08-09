package com.samvad.chat_app.userdetails;


import com.samvad.chat_app.entities.User;
import com.samvad.chat_app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve user from the database using the email (username)
        User user = userService.getUserByEmail(username).orElseThrow();
        // Or if handle optional by checking condition..........
        // System.out.println("APPLICATION : from UserDetailsServiceImpl : USER DETAILS FETCHED : "+ user);
        return new CustomUserDetails(user);
    }

    public CustomUserDetails userDetailsByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(username).orElseThrow();
        return new CustomUserDetails(user);
    }
}
