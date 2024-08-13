package com.ghostface.dev.impl;

import com.ghostface.dev.ContractThread;
import com.ghostface.dev.connection.JChatServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


import java.time.format.DateTimeFormatter;


public final class JChatServerThread extends Thread {

    private final @NotNull JChatServer chat;
    private final @NotNull ServerSocket server;
    private @NotNull Socket socket;

        public JChatServerThread(@NotNull JChatServer chat) {
            this.chat = chat;

            @Nullable ServerSocket server = chat.getServer();

            if (server == null || !server.isBound()) {
                throw new IllegalArgumentException("Server is not running");
            }

            this.server = server;
        }

    private @NotNull MessageImpl messageListener(final @NotNull Socket socket) throws IOException {

        @Nullable UserImpl user = chat.getUsers().stream().filter(user1 -> user1.getSocket().equals(socket)).findFirst().orElse(null);

        if (user == null) {
            throw new IllegalArgumentException("Can´t find user socket");
        }

        @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        @NotNull PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        @NotNull String msg = reader.readLine();

        @NotNull MessageImpl message = new MessageImpl(user, msg);

        @NotNull String date = message.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm"));
        @NotNull String content = "[" + date + "] " + message.getUser().getUsername() + ": " + message.getContent();

        writer.println(content);

        return message;
    }

    private void broadcastMessage(final @NotNull MessageImpl msg) throws IOException {
        @NotNull String date = msg.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm"));
        @NotNull String content = "[" + date + "] " + msg.getUser().getUsername() + ": " + msg.getContent();

        for (@NotNull UserImpl users : chat.getUsers()) {
            if (users != msg.getUser()) {
                @NotNull PrintWriter writer = new PrintWriter(users.getSocket().getOutputStream(), true);
                writer.println(content);
            }
        }
    }

    @Override
    public void run() {
        while (server.isBound()) {
            try {
                this.socket = server.accept();
                System.out.println(socket.getLocalAddress().getHostAddress() + " Trying to connect");

                @NotNull ContractThread authentication = new AuthenticationThread(socket, this, chat);
                authentication.start();

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public void allowMessage(boolean isSucessful) throws IOException {
        if (isSucessful) {
            while (!socket.isClosed()) {
                @NotNull MessageImpl msg = messageListener(socket);
                broadcastMessage(msg);
            }
            socket.close();
        }
    }

}
