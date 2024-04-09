package com.tetris.tetris;

import java.security.SecureRandom;

public class CodeGenerator {

    public static String generateRandomCode() {
        int codeLength = 8; // The length of the generated code
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder codeBuilder = new StringBuilder();

        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < codeLength; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            char codeChar = characters.charAt(randomIndex);
            codeBuilder.append(codeChar);
        }

        return codeBuilder.toString();
    }
}