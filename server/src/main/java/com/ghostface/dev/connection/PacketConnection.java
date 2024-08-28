package com.ghostface.dev.connection;

import com.ghostface.dev.impl.AuthenticationPacket;
import com.ghostface.dev.impl.MessagePacket;
import com.ghostface.dev.impl.RegisterPacket;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.channels.SocketChannel;


public interface PacketConnection {

    static @NotNull PacketConnection getInstance(@NotNull JsonObject object, @NotNull SocketChannel connection) {
        @NotNull String type = object.get("type").getAsString();

        if (type.equals("authentication")) {
            return new AuthenticationPacket(object);
        } else if (type.equals("register")) {
            return new RegisterPacket(object);
        } else if (type.equals("message")) {
            return new MessagePacket(object, connection);
        }
        else {
            throw new IllegalArgumentException("Packet type is not found");
        }
    }

    default @Nullable Object get() {
        return null;
    }

    boolean response(@NotNull ServerSystem system);
}
