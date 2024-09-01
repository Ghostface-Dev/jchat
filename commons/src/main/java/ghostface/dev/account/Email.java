package ghostface.dev.account;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Email implements CharSequence {

    // static initializer

    public static boolean isValid(@NotNull String string) {
        @NotNull String email = string.replace("(dot)", "\\.").replace("(at)", "@");

        if (email.length() > 254) {
            return false;
        } else if (email.split("@").length != 2) {
            return false;
        } else {

            @NotNull String[] parts = email.split("@");
            @NotNull String[] dots = parts[1].split("\\.");

            if (dots.length <2 || dots.length > 3) {
                return false;
            }

            @NotNull String name = parts[0];
            @NotNull String SLD;
            @NotNull String TLD;

            if (dots.length == 3) {
                SLD = dots[0] + "." + dots[1];
                TLD = dots[2];
            } else {
                SLD = dots[0];
                TLD = dots[1];
            }

            if (!name.matches("[a-zA-Z0-9._%+-]{1,64}")) {
                return false;
            } else if (!SLD.matches("^(?!-)[a-zA-Z0-9-_.]{1,63}(?<!-)$")) {
                return false;
            } else if (!TLD.matches("^[^0-9@#\"'\\\\$%]*$")) {
                return false;
            }

        }
        return true;
    }

    public static @NotNull Email parse(@NotNull String string) {
        if (string.length() > 254) {
            throw new IllegalArgumentException("Email too long");
        }

        @NotNull String email = string.replace("(dot)", "\\.").replace("(at)", "@");

        if (isValid(email)) {
            @NotNull String[] parts = email.split("@");
            @NotNull String[] dots = parts[1].split("\\.");

            @NotNull String name = parts[0];
            @NotNull String SLD;
            @NotNull String TLD;

            if (dots.length == 3) {
                SLD = dots[0] + "." + dots[1];
                TLD = dots[2];
            } else {
                SLD = dots[0];
                TLD = dots[1];
            }

            return new Email(name, SLD, TLD);
        } else {
            throw new IllegalArgumentException("Cannot parse '" + string + "' as email");
        }
    }

    // Object

    private final @NotNull String username;
    private final @NotNull String SLD;
    private final @NotNull String TDL;

    public Email(@NotNull String username, @NotNull String sld, @NotNull String tdl) {
        @NotNull String string = username + "@" + sld + "." + tdl;
        if (!isValid(string)) {
            throw new IllegalArgumentException("Email '" + string + "' is not valid");
        }
        this.username = username;
        this.SLD = sld;
        this.TDL = tdl;
    }

    public @NotNull String getUsername() {
        return username;
    }

    public @NotNull String getSLD() {
        return SLD;
    }

    public @NotNull String getTDL() {
        return TDL;
    }

    // natives

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
        return username + "@" + SLD + "." + TDL;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Email email = (Email) object;
        return Objects.equals(username, email.username) && Objects.equals(SLD, email.SLD) && Objects.equals(TDL, email.TDL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, SLD, TDL);
    }

}
