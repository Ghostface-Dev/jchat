package com.ghostface.dev.entity;

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public final class Message {

    private final @NotNull User user;
    private final @NotNull String content;
    private final @NotNull OffsetDateTime time;

    public Message(@NotNull String content, @NotNull User user) {
        this.content = content;
        this.user = user;
        this.time = OffsetDateTime.now();
    }

    public @NotNull User getUser() {
        return user;
    }

    public @NotNull String getContent() {
        return content;
    }

    public @NotNull OffsetDateTime getTime() {
        return time;
    }
}
