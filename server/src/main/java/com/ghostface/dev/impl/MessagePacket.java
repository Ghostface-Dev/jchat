package com.ghostface.dev.impl;

import com.ghostface.dev.connection.ClientConnection;
import com.ghostface.dev.connection.PacketConnection;
import com.ghostface.dev.connection.ServerSystem;
import com.ghostface.dev.entity.Message;
import com.ghostface.dev.entity.User;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.channels.SocketChannel;
import java.time.OffsetDateTime;
import java.util.Optional;

public final class MessagePacket implements PacketConnection {

    private final @NotNull String content;
    private final @NotNull String time;
    private final @NotNull SocketChannel channel;
    private @Nullable Message message;

    public MessagePacket(@NotNull JsonObject object, @NotNull SocketChannel channel) {
        if (!object.has("content") || !object.has("date"))
            throw new IllegalArgumentException("Fields is missing or incorrect");

        this.content = object.get("content").getAsString();
        this.time = object.get("time").getAsString();
        this.channel = channel;
    }

    @Override
    public boolean response(@NotNull ServerSystem system) {

        @NotNull Optional<@NotNull User> optional = system.getUser(channel);

        if (!optional.isPresent()) {
            return false;
        } else {
            @NotNull User user = optional.get();
            message = new Message(user, content, OffsetDateTime.parse(time));
        }

        return true;
    }

    @Override
    public @Nullable Message get() {
        return message;
    }

}
