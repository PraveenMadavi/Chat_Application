package com.samvaad.chat_app.configurations;

import com.samvaad.chat_app.jwt.JwtAuthenticationEntryPoint;
import com.samvaad.chat_app.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class JwtSecurityConfig {
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless JWT-based authentication
                .authorizeHttpRequests(auth -> auth
                        // authenticate endpoints of applications
                        .requestMatchers("/api/crypto/**").permitAll() // Public endpoint
                        .requestMatchers("/api/auth/**").permitAll() // Public endpoint
                        .requestMatchers("/main/**").authenticated()   // Secure endpoint
                        .anyRequest().authenticated()               // All other endpoints require authentication
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)) // Handle unauthorized requests
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before username-password authentication

        return http.build();
    }


    //DaoAuthenticationProvider

}//endregion
