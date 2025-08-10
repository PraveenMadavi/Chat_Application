package com.samvad.chat_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samvad.chat_app.dto.*;
import com.samvad.chat_app.entities.User;
import com.samvad.chat_app.jwt.JwtHelper;
import com.samvad.chat_app.repositories.UserRepository;
import com.samvad.chat_app.services.UserService;
import com.samvad.chat_app.userdetails.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
//import java.security.KeyPair;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository; // repository

    @Autowired
    private UserService userService;   // service

    @Autowired
    private JwtHelper jwtHelper; // component

    @Autowired
    private AuthenticationManager authenticationManager;

    private EncryptedUserRequest encryptedUserRequest;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody EncryptedUserRequest encryptedUserRequest,
                                      HttpServletRequest request,
                                      HttpServletResponse response
    ) throws Exception {
        log.info("CLIENT TRYING TO REGISTER USER ....");
        // Get session ID from request
        HttpSession session = request.getSession(false);

        if (session == null) {
            log.error("sessionId is null.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("SERVER IS EXPECTING JSESSIONID FROM THE CLIENT SIDE.");
        }
        String sessionId = session.getId();

        System.out.println("Session ID received: >>>> " + sessionId);
        System.out.println("payload : " + encryptedUserRequest.toString());
        System.out.println("Trying to get aesKey by session of httpServletRequest...");

        byte[] keyBytes = (byte[]) session.getAttribute("aesKey");
        if (keyBytes == null) {
            System.out.println("Session ID: " + session.getId());
            System.out.println("aesKey key not found in session.");
            throw new IllegalStateException("AES key not found in session");
        }
        SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

        IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(encryptedUserRequest.getIv()));
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, iv);

        byte[] decryptedData = aesCipher.doFinal(Base64.getDecoder().decode(encryptedUserRequest.getEncryptedPayload()));
        String payload = new String(decryptedData, StandardCharsets.UTF_8);

        UserRegistrationDto userInfo = new ObjectMapper().readValue(payload, UserRegistrationDto.class);

        if (userRepository.findByEmail(userInfo.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }


        User user = new User();
        user.setUsername(userInfo.getName());
        user.setEmail(userInfo.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userInfo.getPassword()));

        userRepository.save(user);
        System.out.println("User saved successfully.");

        return ResponseEntity.ok("User registered successfully");  // edit later, don't sent userInfo
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody EncryptedLoginRequest encryptedLoginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        HttpSession session = request.getSession(false);
        if (session==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("SERVER IS EXPECTING JSESSIONID FROM THE CLIENT SIDE.");
        }

        try {
            log.info("Login attempt - Session ID: {}", session.getId());

            // 1. Verify session key exists
            byte[] keyBytes = (byte[]) session.getAttribute("aesKey");
            if (keyBytes == null) {
                log.error("No AES key found in session");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Session expired - please reload the page");
            }

            // 2. Validate encryptedLoginRequest payload
            if (encryptedLoginRequest.getIv() == null || encryptedLoginRequest.getEncryptedPayload() == null) {
                return ResponseEntity.badRequest().body("Missing required fields");
            }

            // 3. Decode IV and payload
            byte[] ivBytes = Base64.getDecoder().decode(encryptedLoginRequest.getIv());
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedLoginRequest.getEncryptedPayload());

            // 4. Initialize cipher
            SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(ivBytes));

            // 5. Decrypt and verify
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String decryptedJson = new String(decryptedBytes, StandardCharsets.UTF_8);

            LoginDto loginDto = new ObjectMapper().readValue(decryptedJson, LoginDto.class);

            // 6. Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String jwtToken = jwtHelper.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(jwtToken));

        } catch (BadPaddingException e) {
            log.error("Decryption failed - padding error", e);
            return ResponseEntity.badRequest().body("Invalid encryption parameters");
        } catch (Exception e) {
            log.error("Login processing error", e);
            return ResponseEntity.internalServerError().body("Login failed");
        }
    }
}

