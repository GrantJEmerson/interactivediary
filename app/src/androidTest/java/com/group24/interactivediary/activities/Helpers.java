package com.group24.interactivediary.activities;

import java.util.Random;

public class Helpers {
    public static String genTestCredential(int length) {
        StringBuilder login = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int r = (int)(Math.random() * 26);
            login.append((i % 2 == 0 ? (char)('A' + r) : (char)('a' + r)));
        }
        return login.toString();
    }
}
