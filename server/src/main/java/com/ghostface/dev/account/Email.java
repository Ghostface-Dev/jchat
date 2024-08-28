package com.ghostface.dev.account;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class Email implements CharSequence {

    // static initializer

    public static boolean validate(@NotNull String email) {

        @NotNull String string = email.replace("(dot)", "\\.").replace("(at)", "@");
        if (string.length() > 254)
            return false;

        @NotNull String[] parts = string.split("@");
        if (parts.length != 2)
            return false;

        @NotNull String username = parts[0];
        if (!username.matches("^[a-zA-Z0-9]{1,64}$"))
            return false;

        @NotNull String[] rest = parts[1].split("\\.", 2);

        @NotNull String sld = rest[0];
        if (!sld.matches("^[a-zA-Z0-9]{1,64}$"))
            return false;

        @NotNull String tld = rest[1];
        return tld.matches("^[a-z.]{2,63}$") && tld.split("\\.").length <= 2;
    }


    public static @Nullable Email create(@NotNull String username, @NotNull String SLD, @NotNull String TDL) {

        if (SLD.startsWith("@")) {
            SLD = SLD.replaceFirst("@", "");
        }

        if (TDL.startsWith(".")) {
            TDL = TDL.replaceFirst("\\.", "");
        }

        @NotNull String email = username + "@" + SLD + "." + TDL;

        if (!validate(email)) {
            return null;
        }

        return new Email(username, SLD, TDL);
    }

    // Object

    private final @NotNull String username;
    private final @NotNull String SLD;
    private final @NotNull String TDL;

    Email(@NotNull String username, @NotNull String sld, @NotNull String tdl) {
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
}
