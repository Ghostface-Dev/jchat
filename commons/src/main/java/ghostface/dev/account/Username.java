package ghostface.dev.account;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Username implements CharSequence {

    // Static

    public static boolean isValid(@NotNull String username) {
        if (username.isEmpty()) {
            return false;
        } else if (!(username.length() >= 3 && username.length() <=14)) {
            return false;
        } else if (!Character.isLetter(username.charAt(0))) {
            return false;
        } else if (!username.matches("^[a-zA-Z0-9._-]+$")) {
            return false;
        }
        return true;
    }

    public static @NotNull Username parse(@NotNull String string) {
        if (!isValid(string)) {
            throw new IllegalArgumentException("Cannot parse '" + string + "' as Username");
        }

        return new Username(string);
    }

    // Object

    private final @NotNull String string;

    public Username(@NotNull String string) {
        if (!isValid(string)) {
            throw new IllegalArgumentException("Username is not valid");
        }

        this.string = string;
    }

    // Natives

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

    @Override
    public @NotNull String toString() {
        return string;
    }

    // Natives

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Username username = (Username) object;
        return Objects.equals(string, username.string);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(string);
    }
}
