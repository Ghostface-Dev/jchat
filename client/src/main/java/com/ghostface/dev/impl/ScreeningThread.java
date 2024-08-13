package com.ghostface.dev.impl;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public final class ScreeningThread extends Thread {

    private final @NotNull Socket socket;

    public ScreeningThread(@NotNull Socket socket) {
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("Socket is not active");
        }

        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            @NotNull PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            @NotNull BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter the username: ");
            @NotNull String username = input.readLine();

            writer.println(username);

            boolean isAuthenticated = Boolean.parseBoolean(reader.readLine());

            if (!isAuthenticated) {
                System.out.println("Username already exist");
            } else {
                System.out.println("welcome " + username);
            }

        } catch (IOException e) {
            System.err.println("Failed to authenticate, try later");;
        }
    }
}
