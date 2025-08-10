package com.samvad.chat_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ChatAppApplication {
	public static final Logger logger = LoggerFactory.getLogger(ChatAppApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(ChatAppApplication.class, args);
		System.out.println("********************************");
		System.out.println("Application started successfully  * * *");
		System.out.println("********************************");
		logger.info("Chat Application is running . . . !");

	}

}
