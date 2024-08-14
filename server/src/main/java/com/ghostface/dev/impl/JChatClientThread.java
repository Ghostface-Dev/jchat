package com.ghostface.dev.impl;

import com.ghostface.dev.JChatServer;
import com.ghostface.dev.entity.Message;
import com.ghostface.dev.entity.User;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import java.time.format.DateTimeFormatter;
import java.util.Map;

final class JChatClientThread extends Thread {

    private final @NotNull Socket socket;
    private final @NotNull JChatServer chatServer;

    public JChatClientThread(@NotNull Socket socket, @NotNull JChatServer chatServer) {
        @NotNull Socket client = socket;

        if (!client.isBound()) {
            throw new IllegalArgumentException("Client is not active");
        }

        this.socket = client;
        this.chatServer = chatServer;
    }

    private boolean clientAuthentication(@NotNull Socket socket) throws IOException {

        if (chatServer.getUsers().containsKey(socket)) {
            System.err.println(socket.getLocalAddress().getHostAddress() + " Already authenticated");
            socket.close();
            return false;
        }

        System.out.println(socket.getLocalAddress().getHostAddress() + " Trying authentication");

        try {
            @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            @NotNull PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            @NotNull String username = reader.readLine();
            @NotNull User user = new User(username, socket);

            if (chatServer.getUsers().containsValue(user)) {
                writer.println(false);
                socket.close();
                return false;
            } else {
                chatServer.getUsers().put(socket, user);
                writer.println(true);
                broadcastMessage(user.getUsername() + " joined", chatServer.getUsers());
                return true;
            }

        } catch (SocketException e) {
            System.out.println(socket.getLocalAddress().getHostAddress() + " Disconnect: " + e.getMessage());
            socket.close();
            chatServer.getUsers().remove(socket);
            return false;
        }
    }

    private void broadcastMessage(@NotNull String msg ,@NotNull Map<@NotNull Socket ,@NotNull User> users) throws IOException {

        for (@NotNull Socket sockets : users.keySet()) {
            if (!sockets.isConnected()) {
                users.remove(sockets);
            }
        }

        for (@NotNull User user: users.values()) {
            @NotNull PrintWriter writer = new PrintWriter(user.getSocket().getOutputStream(),true);
            writer.println(msg);
        }

    }

    private void broadcastMessage(@NotNull Message msg , @NotNull Map<@NotNull Socket ,@NotNull User> users) throws IOException {

        for (@NotNull Socket sockets : users.keySet()) {
            if (!sockets.isConnected()) {
                users.remove(sockets);
            }
        }

        @NotNull String time = msg.getTime().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"));
        @NotNull String content = "[" + time + "] " + msg.getUser().getUsername() + ": " + msg.getContent();

        for (@NotNull User user: users.values()) {
            @NotNull PrintWriter writer = new PrintWriter(user.getSocket().getOutputStream(),true);
            if (user != msg.getUser()) {
                writer.println(content);
            }
        }

    }

    private @NotNull Message messageListener(@NotNull Socket socket, @NotNull Map<@NotNull Socket, User> users) throws IOException {

        @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        @NotNull String msg = reader.readLine();

        @NotNull Message message = new Message(msg, users.get(socket));

        return message;

    }

    @Override
    public void run() {
        try {

            if (clientAuthentication(socket)) {
                System.out.println(socket.getLocalAddress().getHostAddress() + " sucessful authenticated");
                while (socket.isConnected()) {
                    @NotNull Message msg = messageListener(socket, chatServer.getUsers());
                    broadcastMessage(msg, chatServer.getUsers());
                }
            }

        } catch (IOException e) {
            System.err.println(socket.getLocalAddress().getHostAddress() + " Disconnect: " + e.getMessage());
        }
    }
}
