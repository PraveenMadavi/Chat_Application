package com.eazybyts.chat_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableCaching
public class ChatAppApplication {
	public static final Logger logger = LoggerFactory.getLogger(ChatAppApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(ChatAppApplication.class, args);
		System.out.println();
		System.out.println("      88    88    88888888   88         88          888888                          *                ");
		System.out.println("      88    88    88         88         88         88    88                           *      +            ");
		System.out.println("      88888888    888888     88         88         88    88                              * +    +            +");
		System.out.println("      88    88    88         88         88         88    88                           *      +             ");
		System.out.println("      88    88    88888888   88888888   88888888    888888                          *                  ");
		System.out.println();
		logger.info("Chat Application is running . . . !");

	}

}
