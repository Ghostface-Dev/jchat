package com.ghostface.dev.impl;

import com.ghostface.dev.connection.PacketConnection;
import com.ghostface.dev.connection.ServerSystem;
import com.ghostface.dev.entity.User;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public final class AuthenticationPacket implements PacketConnection {

    private final @NotNull String username;
    private final @NotNull String password;

    public AuthenticationPacket(@NotNull JsonObject object) {

        if (!object.has("username") || !object.has("password"))
            throw new IllegalArgumentException("Field is missing or incorrect");

        this.username = object.get("username").getAsString();
        this.password = object.get("password").getAsString();
    }

    @Override
    public boolean response(@NotNull ServerSystem system) {

        for (@NotNull User user : system.getUsers().keySet()) {
            if (user.getUsername().equals(username)) {
                @NotNull User user1 = user;
                @NotNull String password = system.getUsers().get(user1);
                return password.equals(this.password);
            }
        }

        return false;
    }

}
