package com.samvad.chat_app.controllers;

import com.samvad.chat_app.dto.EncryptedAesKey;
import com.samvad.chat_app.dto.EncryptedMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {
    private final KeyPair rsaKeyPair;

    public CryptoController(KeyPair rsaKeyPair) {
        this.rsaKeyPair = rsaKeyPair;
    }

    @GetMapping("/get-public-key")
    public String getRsaPublicKey() {
        System.out.println("Client is tyring to fetch public key");
        // Get the public key from your RSA key pair
        PublicKey publicKey = rsaKeyPair.getPublic();
        System.out.println("PublicKey : >" + publicKey);
        // Convert the public key to Base64 encoded string and return
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println("public key encoded with base64 (to string) : " + publicKeyString);
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    @PostMapping("/set-aes-key")
    public ResponseEntity<?> setAesKey(@RequestBody EncryptedAesKey encryptedAesKey,
                                       HttpServletRequest request,
                                       HttpServletResponse response
    ) throws Exception {
        HttpSession session = request.getSession();
        System.out.println("Trying to set aesKey by the client");
        //Decrypt AES key with RSA private key
        try {
//            Cipher rsaCipher = Cipher.getInstance("RSA");
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
//            Cipher rsaCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");


            rsaCipher.init(Cipher.DECRYPT_MODE, rsaKeyPair.getPrivate());
            byte[] aesKeyBytes = rsaCipher.doFinal(Base64.getDecoder().decode(encryptedAesKey.getEncryptedAesKey()));

            System.out.println("Session ID: " + session.getId());
            session.setAttribute("aesKey", aesKeyBytes);

            //Store aesKey in session
//            session.setAttribute("aesKey", aesKey);  // don't store row key

            System.out.println("aes key set successfully....................");

            return ResponseEntity.ok(session.getId());

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error while setting aesKey...");
            throw new RuntimeException("Error while setting aesKey " + e);
        }

    }

    @PostMapping("/encrypted-message")
    public ResponseEntity<?> receiveEncryptedData(
//            @RequestHeader("X-Session-ID") String sessionId,
            @RequestBody EncryptedMessage request,
            HttpSession session
    ) throws Exception {
        System.out.println("Now trying to get message......");
        // 1. Retrieve AES key from cache
//        SecretKey aesKey = (SecretKey) session.getAttribute("aesKey");
//        SecretKey aesKey = new SecretKeySpec((byte[]) session.getAttribute("aesKey"), "AES");

        byte[] keyBytes = (byte[]) session.getAttribute("aesKey");
        if (keyBytes == null) {
            System.out.println("Session ID: " + session.getId());
            throw new IllegalStateException("AES key not found in session");
        }
        SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");


        try {
            // Decrypt AES-encrypted message
            IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(request.getIv()));
            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey, iv);

            byte[] decryptedData = aesCipher.doFinal(Base64.getDecoder().decode(request.getEncryptedMessage()));
            String decryptedText = new String(decryptedData, StandardCharsets.UTF_8);

            System.out.println("Message from the client: >>>> " + decryptedText);

            // Send plain confirmation back
            return ResponseEntity.ok("Success");


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing message");
        }
    }
}

