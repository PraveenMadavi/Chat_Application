package com.eazybyts.chat_app.components;


import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class Clients {
    private static final HashMap<Integer, byte[]> clientHashMap = new HashMap<>();

    public static Integer setAesKey(byte[] aesKey) {
        int token = TokenService.getUniqueToken();
        clientHashMap.put(token, aesKey);
        return token;
    }

    public static byte[] getAesKey(Integer token){
        return clientHashMap.get(token);
    }

    public static void deleteByToken(Integer token){
        clientHashMap.remove(token);
    }




}
