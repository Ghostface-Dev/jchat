package com.ghostface.dev.entity;

import com.ghostface.dev.connection.JChatClient;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public final class User {

    private final @NotNull String username;
    private final @NotNull JChatClient client;
    private final @NotNull OffsetDateTime time;

    public User(@NotNull String username, @NotNull JChatClient client) {
        this.username = username;
        this.client = client;
        this.time = OffsetDateTime.now();
    }

    public @NotNull String getUsername() {
        return username;
    }

    public @NotNull JChatClient getClient() {
        return client;
    }

    public @NotNull OffsetDateTime getTime() {
        return time;
    }

}
