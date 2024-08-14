package com.ghostface.dev.authentication;

import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public class Message {

    private final @NotNull OffsetDateTime time;
    private final @NotNull String content;

    public Message(@NotNull OffsetDateTime time, @NotNull String content) {
        this.time = time;
        this.content = content;
    }

    public @NotNull OffsetDateTime getTime() {
        return time;
    }

    public @NotNull String getContent() {
        return content;
    }
}
