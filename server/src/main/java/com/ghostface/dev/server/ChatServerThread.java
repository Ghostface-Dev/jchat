package com.ghostface.dev.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.ServerSocket;
import java.nio.channels.Selector;

final class ChatServerThread extends Thread {

    private final @NotNull ChatServer chat;
    private final @NotNull ServerSocket server;
    private final @NotNull Selector selector;

    public ChatServerThread(@NotNull ChatServer chat) {
        this.chat = chat;
        @Nullable ServerSocket socket = getChat().getSocket();
        @Nullable Selector selector = getChat().getSelector();

        if ((socket == null || !socket.isBound()) || selector == null) {
            throw new IllegalArgumentException("Chat is not active");
        }

        this.server = socket;
        this.selector = selector;
    }

    @Override
    public void run() {
        while (server.isBound() && selector.isOpen()) {

        }
    }

    public @NotNull ChatServer getChat() {
        return chat;
    }
}
