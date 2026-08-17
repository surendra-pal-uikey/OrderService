package org.sp.orderservice.utils;

import java.security.SecureRandom;

public class IdGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate(String prefix, int randomLength) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < randomLength; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}