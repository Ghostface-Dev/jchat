package com.ghostface.dev.account;


import com.ghostface.dev.entity.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Account {

    // Object

    private final @NotNull Email email;
    private final @NotNull String password;
    private final @NotNull User user;

    public Account(@NotNull Email email, @NotNull String password, @NotNull User user) {
        this.email = email;
        this.password = password;
        this.user = user;
    }

    public @NotNull User getUser() {
        return user;
    }

    public @NotNull String getPassword() {
        return password;
    }

    public @NotNull Email getEmail() {
        return email;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Account account = (Account) object;
        return Objects.equals(email, account.email) && Objects.equals(user, account.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, user);
    }
}
