package com.ghostface.dev.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

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

    // natives

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Message message = (Message) object;
        return Objects.equals(user, message.user);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user);
    }

    @Override
    public @NotNull String toString() {
        @NotNull String time = this.time.format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"));
        return "[" + time + "] " + user.getUsername() + ": " + content;
    }
}
