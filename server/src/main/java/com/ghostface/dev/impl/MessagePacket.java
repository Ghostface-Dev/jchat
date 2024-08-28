package com.ghostface.dev.impl;

import com.ghostface.dev.comunication.PacketComunication;
import com.ghostface.dev.connection.ServerSystem;
import com.ghostface.dev.entity.Message;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.channels.SocketChannel;


public final class MessagePacket extends PacketComunication {

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
        return false;
    }

    @Override
    public boolean isAuthentication() {
        return false;
    }

    @Override
    public boolean isRegister() {
        return false;
    }

    @Override
    public boolean isMessage() {
        return true;
    }


}
