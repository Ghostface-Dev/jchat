package ghostface.dev.account;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class Password implements CharSequence {


    // static

    public static boolean validate(@NotNull String string) {
        return string.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,20}$");
    }

    // Object

    private final @NotNull String password;

    public Password(@NotNull String password) {
        this.password = password;
    }

    // Native
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
        return toString().subSequence(start, end);
    }

    @Override
    public @NotNull String toString() {
        return password;
    }
}
