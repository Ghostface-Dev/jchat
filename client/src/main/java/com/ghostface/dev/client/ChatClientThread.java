package com.ghostface.dev.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

final class ChatClientThread extends Thread {

    private final @NotNull ChatClient client;
    private final @NotNull Socket socket;
    private final @NotNull Selector selector;

    public ChatClientThread(@NotNull ChatClient client) {
        this.client = client;

        @Nullable Socket socket = client.getSocket();
        @Nullable Selector selector = client.getSelector();

        if ((socket == null || !socket.isBound()) || selector == null) {
            throw new IllegalArgumentException("Client is not active");
        }

        this.socket = socket;
        this.selector = selector;
    }

    @Override
    public void run() {
       while (socket.isConnected() && selector.isOpen()) {

       }
    }

    public @NotNull ChatClient getClient() {
        return client;
    }
}
