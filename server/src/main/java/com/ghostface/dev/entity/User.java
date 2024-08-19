package com.ghostface.dev.entity;

import com.ghostface.dev.connection.JChatClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Objects;

public final class User {

    private final @NotNull JChatClient client;
    private final @NotNull String username;
    private final @NotNull OffsetDateTime time;

    public User(@NotNull String username, @NotNull JChatClient client) {
        this.client = client;
        this.username = username;
        this.time = OffsetDateTime.now();
    }

    public @NotNull String getUsername() {
        return username;
    }

    public @NotNull OffsetDateTime getTime() {
        return time;
    }

    public @NotNull JChatClient getClient() {
        return client;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull User user = (User) object;
        return Objects.equals(client, user.client) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, username);
    }
}
