package com.eazybyts.chat_app.controllers;

import com.eazybyts.chat_app.components.AESUtil;
import com.eazybyts.chat_app.dto.EncryptedLoginRequest;
import com.eazybyts.chat_app.dto.EncryptedUserRequest;
import com.eazybyts.chat_app.dto.LoginDto;
import com.eazybyts.chat_app.dto.UserRegistrationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eazybyts.chat_app.entities.User;
import com.eazybyts.chat_app.jwt.JwtHelper;
import com.eazybyts.chat_app.components.Clients;
import com.eazybyts.chat_app.repositories.jpa.UserRepository;
import com.eazybyts.chat_app.services.UserService;
import com.eazybyts.chat_app.userdetails.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
    private AESUtil aesUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    private EncryptedUserRequest encryptedUserRequest;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody EncryptedUserRequest encryptedUserRequest
    ) throws Exception {
        log.info("CLIENT TRYING TO REGISTER USER ....");
        int token = encryptedUserRequest.getToken();

        System.out.println("payload : " + encryptedUserRequest.toString());
        System.out.println("Trying to get aesKey by session of httpServletRequest...");

//        byte[] aesKeyBytes = (byte[]) session.getAttribute("aesKey");
        byte[] aesKeyBytes = Clients.getAesKey(token);

        if (aesKeyBytes == null) {
            System.out.println("aesKey key not found in clients list of mapped with aesKey.");
            throw new IllegalStateException("AES key not found in memory.");
        }
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

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

//.................................................................................................


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody EncryptedLoginRequest encryptedLoginRequest
    ) {
        log.info("login api hit ");

        int token = encryptedLoginRequest.getToken();

        try {
//            byte[] aesKeyBytes = (byte[]) session.getAttribute("aesKey");
            byte[] aesKeyBytes = Clients.getAesKey(token);
            if (aesKeyBytes == null) {
                log.error("No AES key found in memory");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("aesKey not found in memory with provided token.");
            }

            // Validate encryptedLoginRequest payload
            if (encryptedLoginRequest.getIv() == null || encryptedLoginRequest.getEncryptedPayload() == null) {
                return ResponseEntity.badRequest().body("Missing required fields");
            } //  edit later

            // 3. Decode IV and payload
            byte[] ivBytes = Base64.getDecoder().decode(encryptedLoginRequest.getIv());
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedLoginRequest.getEncryptedPayload());

            // 4. Initialize cipher
            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(ivBytes));

            // 5. Decrypt and verify
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String decryptedJson = new String(decryptedBytes, StandardCharsets.UTF_8);

            LoginDto loginDto = new ObjectMapper().readValue(decryptedJson, LoginDto.class);

            // 6. Authenticate
            CustomUserDetails userDetails = null;
            String jwtToken = null;
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginDto.getUsername(),
                                loginDto.getPassword()
                        )
                );
                userDetails = (CustomUserDetails) authentication.getPrincipal();
                jwtToken = jwtHelper.generateToken(userDetails);
            } catch (AuthenticationException e) {
                throw new RuntimeException("Could not authenticate user : " + loginDto.getUsername() + " : " + e);
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // send response with encrypted user-data with token
            User loggedUser = userRepository.findByEmail(loginDto.getUsername()).orElseThrow();
//            aesUtil.decryptData(loggedUser.toString(),Base64.getEncoder().encodeToString(aesKey.getEncoded()),new IvParameterSpec(ivBytes));
            // client side app should store the token in cookie memory
            return ResponseEntity.ok(new LogInResponse(jwtToken,loggedUser)); // sent userid.........................

        } catch (BadPaddingException e) {
            log.error("Decryption failed - padding error", e);
            return ResponseEntity.badRequest().body("Invalid encryption parameters");
        } catch (Exception e) {
            log.error("Login processing error", e);
            return ResponseEntity.internalServerError().body("Login failed");
        }
    }


    //DTO
    @Data
    @AllArgsConstructor
    public static class LogInResponse {
        private String token;
        User user;
    }


}//END_OF_MAIN_CLASS
