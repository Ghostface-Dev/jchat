package com.ghostface.dev.impl;

import com.ghostface.dev.ContractThread;
import com.ghostface.dev.connection.JChatServer;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

final class AuthenticationThread extends ContractThread {

    private final @NotNull Socket socket;
    private final @NotNull JChatServerThread thread;
    private final @NotNull JChatServer chat;

    public AuthenticationThread(@NotNull Socket socket, @NotNull JChatServerThread thread, @NotNull JChatServer chat) {
        this.socket = socket;
        this.thread = thread;
        this.chat = chat;
    }

    @Override
    public void run() {
        try {
            @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            @NotNull PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("starting authentication to " + socket.getLocalAddress().getHostAddress());

            // get username string from client
            @NotNull String username = reader.readLine();
            @NotNull UserImpl user = new UserImpl(username, socket);

            if (chat.getUsers().contains(user)) {
                writer.println(false);
                System.out.println("unsuccessful authentication of " + socket.getLocalAddress().getHostAddress());
                isSuccessful = false;
            } else {
                chat.getUsers().add(user);
                writer.println(true);
                System.out.println("successful authentication of " + socket.getLocalAddress().getHostAddress());
                System.out.println(user.getUsername() + " joined");
                thread.allowMessage(true);
            }

        } catch (SocketException ignore) {
            isSuccessful = false;
            System.out.println(socket.getLocalAddress().getHostAddress() + " Connection lost");
        } catch (IOException e) {
            System.err.println(socket.getLocalAddress().getHostAddress() + ": " + e.getMessage());
            isSuccessful = false;
        }
    }

    @Override
    public boolean isSuccessful() {
        return isSuccessful;
    }
}
