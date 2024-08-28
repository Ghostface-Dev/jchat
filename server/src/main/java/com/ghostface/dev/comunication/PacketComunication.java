package com.ghostface.dev.comunication;

import com.ghostface.dev.connection.ServerSystem;
import com.ghostface.dev.impl.AuthenticationPacket;
import com.ghostface.dev.impl.MessagePacket;
import com.ghostface.dev.impl.RegisterPacket;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.nio.channels.SocketChannel;


public abstract class PacketComunication {

    public static @NotNull PacketComunication getInstance(@NotNull JsonObject object, @NotNull SocketChannel channel) {
        if (!object.has("type"))
            throw new IllegalArgumentException("Field 'type' is missing or incorrect");
        if (!channel.socket().isBound()) {
            throw new IllegalArgumentException("Channel is not active");
        }

        @NotNull String type = object.get("type").getAsString();

        if (type.equals("authentication")) {
            return new AuthenticationPacket(object);
        } else if (type.equals("register")) {
            return new RegisterPacket(object);
        } else if (type.equals("message")) {
            return new MessagePacket(object, channel);
        } else {
            throw new IllegalArgumentException("Packet type is not found");
        }
    }

    public abstract boolean response(@NotNull ServerSystem system);

    public abstract boolean isAuthentication();
    public abstract boolean isRegister();
    public abstract boolean isMessage();
}
