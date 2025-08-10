package com.samvad.chat_app.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @PostMapping("/api/auth/test")
    public String test(@RequestBody HttpServletRequest request){
        try {
            HttpSession session = request.getSession(false);
            System.out.println("While testing ..." + session.getId());
            return "Test successfully";
        } catch (Exception e) {
            System.out.println("session not received");
            throw new RuntimeException(e);
        }

    }
}
