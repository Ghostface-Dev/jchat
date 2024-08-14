package com.ghostface.dev.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Socket;
import java.util.Objects;

public final class User {

    private final @NotNull String username;
    private final @NotNull Socket socket;

    public User(@NotNull String username, @NotNull Socket socket) {
        this.username = username;
        this.socket = socket;
    }

    public @NotNull String getUsername() {
        return username;
    }

    public @NotNull Socket getSocket() {
        return socket;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull User user = (User) object;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
