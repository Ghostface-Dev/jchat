package com.ghostface.dev.entity;

import com.ghostface.dev.connection.ClientConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Objects;

public final class User {

    private final @NotNull String username;
    private final @NotNull OffsetDateTime dateTime;
    private final @NotNull ClientConnection connection;

    public User(@NotNull String username, @NotNull ClientConnection connection) {
        this.username = username;
        this.connection = connection;
        dateTime = OffsetDateTime.now();
    }

    public @NotNull String getUsername() {
        return username;
    }

    public @NotNull OffsetDateTime getDateTime() {
        return dateTime;
    }

    public @NotNull ClientConnection getConnection() {
        return connection;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull User user = (User) object;
        return Objects.equals(username, user.username) && Objects.equals(connection, user.connection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, connection);
    }
}
