package com.samvaad.chat_app.components;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtil {

    public static String generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey key = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String encrypt(String algorithm, String input, String key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, String key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }

    //decrypt messages, userInfo,... with the help if ivParaSpec
    public static String decryptData(String data,String aesKey, IvParameterSpec iv)throws Exception{
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(aesKey),"AES");
        cipher.init(Cipher.DECRYPT_MODE,keySpec,iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(data));
        return new String(plainText);
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}