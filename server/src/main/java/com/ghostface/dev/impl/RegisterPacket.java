package com.ghostface.dev.impl;

import com.ghostface.dev.comunication.PacketComunication;
import com.ghostface.dev.connection.ServerSystem;
import com.ghostface.dev.entity.User;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public final class RegisterPacket extends PacketComunication {

    private final @NotNull String username;

    public RegisterPacket(@NotNull JsonObject object) {

        if (!object.has("username") || !object.has("password"))
            throw new IllegalArgumentException("Field is missing or incorrect");

        this.username = object.get("username").getAsString();
    }

    @Override
    public boolean response(@NotNull ServerSystem system) {


        return true;
    }

    @Override
    public boolean isAuthentication() {
        return false;
    }

    @Override
    public boolean isRegister() {
        return true;
    }

    @Override
    public boolean isMessage() {
        return false;
    }


}
