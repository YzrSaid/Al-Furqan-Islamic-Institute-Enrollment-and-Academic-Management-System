package com.example.testingLogIn.Services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;

public class NonModelServices {

    public static String forLikeOperator(String toFormat){
        return "%"+toFormat.toLowerCase()+"%";
    }

    public static double adjustDecimal(double value){
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double zeroIfLess(double value){
        return value < 0 ? 0.0d : value;
    }

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String DIGITS = "0123456789";

    private static final String PASSWORD_ALLOW =
            CHAR_LOWER + CHAR_UPPER + DIGITS;

    private static SecureRandom random = new SecureRandom();

    public static String generate(int length) {
        if (length < 8) throw new IllegalArgumentException("Password too short");

        StringBuilder sb = new StringBuilder(length);

        // Ensure at least one character from each group
        sb.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        sb.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        sb.append(DIGITS.charAt(random.nextInt(DIGITS.length())));

        // Fill remaining with random characters
        for (int i = 4; i < length; i++) {
            sb.append(PASSWORD_ALLOW.charAt(random.nextInt(PASSWORD_ALLOW.length())));
        }

        return shuffleString(sb.toString());
    }

    private static String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int j = random.nextInt(chars.length);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    public static DateTimeFormatter getDateTimeFormatter(){
        return DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
    }
}
