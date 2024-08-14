package com.ghostface.dev.impl;

import com.ghostface.dev.JChatServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JChatServerThread extends Thread {

    private final @NotNull JChatServer chatServer;
    private final @NotNull ServerSocket server;


    public JChatServerThread(@NotNull JChatServer chatServer) {
        this.chatServer = chatServer;

        @Nullable ServerSocket socket = chatServer.getServer();

        if (socket == null) {
            throw new IllegalArgumentException("The chat Server is not active");
        }

        this.server = socket;

    }

    @Override
    public void run() {
        while (server.isBound()) {
            try {
                @NotNull Socket socket = server.accept();

                @NotNull Thread thread = new JChatClientThread(socket, chatServer);
                thread.start();

            } catch (IOException e) {
                System.err.println("Failed to accept:" + e.getMessage());
            }
        }
    }

    public @NotNull JChatServer getChatServer() {
        return chatServer;
    }

    public @NotNull ServerSocket getServer() {
        return server;
    }
}
