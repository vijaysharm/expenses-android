package com.vijaysharma.expenses.misc;

public class Checks {
    public static boolean isUsernameValid(String username) {
        return username != null && !username.trim().isEmpty();
    }

    public static boolean isPasswordValid(String password) {
        return password != null && !password.trim().isEmpty();
    }
}
