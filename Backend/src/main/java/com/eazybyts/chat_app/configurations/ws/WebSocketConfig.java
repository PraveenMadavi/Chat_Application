package com.eazybyts.chat_app.configurations.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String[] ALLOWED_ORIGINS = {
            "https://chat-application-ui-black.vercel.app",
            "http://localhost:5173",
            "http://localhost:[0-9]+" // Allow any localhost port for development
    };

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");    //  >>>  server broadcast ...
        config.setApplicationDestinationPrefixes("/app");//  <<<  user published (sent) Data
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")      //    >>>  <<< Handshake for register  ..........
                .setAllowedOriginPatterns(ALLOWED_ORIGINS)
                .withSockJS();
        // Also add this for non-SockJS fallback
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(ALLOWED_ORIGINS);
    }


}
