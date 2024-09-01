package ghostface.dev.account;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Password implements CharSequence {

    // Static

    public static boolean isValid(@NotNull String string) {
        return string.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,20}$");
    }

    public static @NotNull Password parse(@NotNull String string) {
        if (!isValid(string)) {
            throw new IllegalArgumentException("Cannot parse '" + string + "' as password");
        }

        return new Password(string);
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

    // Natives

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Password password1 = (Password) object;
        return Objects.equals(password, password1.password);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(password);
    }
}
