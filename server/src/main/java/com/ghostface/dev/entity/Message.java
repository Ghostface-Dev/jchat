package com.ghostface.dev.entity;

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class Message {

    private final @NotNull User user;
    private final @NotNull String content;
    private final @NotNull OffsetDateTime time;

    public Message(@NotNull User user, @NotNull String content) {
        this.user = user;
        this.content = content;
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

    @Override
    public String toString() {
        @NotNull String time = this.time.format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"));

        return "[" + time + "] " + user.getUsername() + ": " + content;
    }
}
