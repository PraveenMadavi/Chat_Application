package com.samvad.chat_app.jwt;

import com.samvad.chat_app.userdetails.CustomUserDetails;
import com.samvad.chat_app.userdetails.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Extract Authorization header
        String requestHeader = request.getHeader("Authorization");
        logger.info("Authorization Header: {}", requestHeader);

        String username = null;
        String token = null;

        // Check if the Authorization header is valid and starts with "Bearer "
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            token = requestHeader.substring(7); // Remove "Bearer " prefix

            try {
                username = jwtHelper.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.warn("Illegal argument while fetching the username from token: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                logger.warn("JWT token is expired: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                logger.warn("Invalid JWT token: {}", e.getMessage());
            } catch (Exception e) {
                logger.error("Unexpected error while processing token: {}", e.getMessage());
            }
        } else {
            logger.info("Invalid Authorization header value");
        }

        // Validate token and set authentication context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            CustomUserDetails userDetails = userDetailsService.userDetailsByUsername(username);

            if (jwtHelper.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null,  userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("User '{}' authenticated successfully", username);
            } else {
                logger.warn("Token validation failed for user '{}'", username);
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}