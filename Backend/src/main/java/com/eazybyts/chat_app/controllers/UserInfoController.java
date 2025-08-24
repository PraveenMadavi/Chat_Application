package com.eazybyts.chat_app.controllers;

import com.eazybyts.chat_app.entities.User;
import com.eazybyts.chat_app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserInfoController {  // this controller not in use so delete this later
    @Autowired
    UserService userService;

    @PostMapping("/info")
    public User getInfo(@RequestBody Long userId){
        return userService.getUserById(userId);
    }

}
