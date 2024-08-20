package com.ghostface.dev.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Socket;

final class ChatClientThread extends Thread {

    private final @NotNull ChatClient client;
    private final @NotNull Socket socket;

    public ChatClientThread(@NotNull ChatClient client) {
        this.client = client;

        @Nullable Socket socket = client.getSocket();

        if (socket == null || !socket.isBound()) {
            throw new IllegalArgumentException("Client is not active");
        }

        this.socket = socket;
    }

    @Override
    public void run() {
       while (socket.isConnected()) {

       }
    }

    public @NotNull ChatClient getClient() {
        return client;
    }
}
