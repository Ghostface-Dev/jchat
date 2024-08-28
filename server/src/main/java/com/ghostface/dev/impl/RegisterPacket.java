package com.ghostface.dev.impl;

import com.ghostface.dev.connection.PacketConnection;
import com.ghostface.dev.connection.ServerSystem;
import com.ghostface.dev.entity.Message;
import com.ghostface.dev.entity.User;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RegisterPacket implements PacketConnection {

    private final @NotNull String username;

    public RegisterPacket(@NotNull JsonObject object) {

        if (!object.has("username") || !object.has("password"))
            throw new IllegalArgumentException("Field is missing or incorrect");

        this.username = object.get("username").getAsString();
    }

    @Override
    public boolean response(@NotNull ServerSystem system) {

        for (@NotNull User user : system.getUsers().keySet()) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }

        return true;
    }


}
