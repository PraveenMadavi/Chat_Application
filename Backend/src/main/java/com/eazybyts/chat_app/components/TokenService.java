package com.eazybyts.chat_app.components;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenService {
    private static final SecureRandom random = new SecureRandom();
    private static final Set<Integer> generatedNumbers = new HashSet<>();

    public static int getUniqueToken() {
        int ranNum;
        do {
            ranNum = 100_000 + random.nextInt(900_000);
        } while (!generatedNumbers.add(ranNum));
        System.out.println("Random Keys: "+ generatedNumbers);
        return ranNum;
    }

    public static void clearTokenSet(){
        generatedNumbers.clear();
    }
}
