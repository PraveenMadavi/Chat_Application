package com.samvaad.chat_app.components;

//import lombok.Data;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import java.security.*;
//import java.security.spec.ECGenParameterSpec;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RSAUtil {

    //    static {
//        Security.addProvider(new BouncyCastleProvider());
//    }
//
//    @Data
//    public static class KeyPair {
//        private String publicKey;
//        private String privateKey;
//    }
    private final KeyPair rsaKeyPair;

    public RSAUtil(KeyPair rsaKeyPair) {
        this.rsaKeyPair = rsaKeyPair;
    }

//    public KeyPair generateKeyPair() throws Exception {
//        java.security.KeyPairGenerator generator = java.security.KeyPairGenerator.getInstance("RSA");
//        generator.initialize(2048);
//        java.security.KeyPair keyPair = generator.generateKeyPair();
//
//        KeyPair result = new KeyPair();
//        result.setPublicKey(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
//        result.setPrivateKey(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
//        return result;
//    }

    public PublicKey getPublicKey(String base64PublicKey) throws Exception {
//        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
//        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return rsaKeyPair.getPublic();
    }

    public PrivateKey getPrivateKey(String base64PrivateKey) throws Exception {
//        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        return keyFactory.generatePrivate(spec);
        return rsaKeyPair.getPrivate();
    }

    public String encrypt(String data, String publicKey) throws Exception {
        PublicKey key = getPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public String decrypt(String data, String privateKey) throws Exception {
        PrivateKey key = getPrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
    }

    // decrypt any data that is encrypted by the publicKey
    public String decryptData(String data)throws Exception{
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, rsaKeyPair.getPrivate());
        return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
    }
}