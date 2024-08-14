package com.ghostface.dev.authentication;

import org.jetbrains.annotations.NotNull;

public final class Username implements CharSequence {

    public static boolean validate(@NotNull String username) {
        if (username.isEmpty()) {
            System.err.println("Invalid Username");
            return false;
        } else if (!(username.length() >= 3 && username.length() <=14)) {
            System.err.println("Username must be 3 at 14 characters");
            return false;
        } else if (!Character.isLetter(username.charAt(0))) {
            System.err.println("First index must to be a letter");
            return false;
        } else if (!username.matches("^[a-zA-Z0-9._-]+$")) {
            System.err.println("Invalid character");
            return false;
        }
        return true;
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return toString().subSequence(start,end);
    }
}
