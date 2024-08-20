package com.ghostface.dev.utility;

import org.jetbrains.annotations.NotNull;

public final class Username implements CharSequence {

    public static boolean validate(@NotNull String username) {
        if (username.isEmpty()) {
            System.err.println("Username invalid");
            return false;
        } else if (!(username.length() >= 4 && username.length() <= 12)) {
            System.err.println("Username must have beetwen 4 and 12 characters");
            return false;
        } else if (!Character.isLetter(username.charAt(0))) {
            System.err.println("The first character must to be a letter");
            return false;
        } else if (!username.matches("^[a-zA-Z0-9._-]+$")) {
            System.err.println("Invalid symbols, use: ._-");
            return false;
        } else {
            return true;
        }
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
