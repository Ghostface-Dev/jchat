package com.ghostface.dev.impl;

import com.ghostface.dev.factory.Message;
import com.ghostface.dev.factory.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.time.OffsetDateTime;
import java.util.Objects;

public class MessageImpl implements Message {

    private final @NotNull User user;
    private final @NotNull String content;
    private final @NotNull OffsetDateTime date;

    public MessageImpl(@NotNull User user, @NotNull String content) {
        this.user = user;
        this.content = content;
        this.date = OffsetDateTime.now();
    }

    @Override
    public @NotNull String getContent() {
        return content;
    }

    @Override
    public @NotNull User getUser() {
        return user;
    }

    @Override
    public @NotNull OffsetDateTime getDate() {
        return date;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull MessageImpl message = (MessageImpl) object;
        return Objects.equals(user, message.user);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(user);
    }
}
