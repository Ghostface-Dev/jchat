package com.ghostface.dev;

import com.ghostface.dev.authentication.Message;
import com.ghostface.dev.authentication.Username;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class ChatRoom {

    private final @NotNull InetSocketAddress address;
    private @Nullable Socket socket;

    public ChatRoom(@NotNull InetSocketAddress address) {
        this.address = address;
    }

    private boolean authentication(@NotNull Socket socket) throws IOException {

        @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        @NotNull BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        @NotNull PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

        System.out.print("Enter the username: ");
        @NotNull String username = in.readLine();

        while (!Username.validate(username)) {
            System.out.print("Enter the username: ");
            username = in.readLine();
        }

        writer.println(username);

        boolean response = Boolean.parseBoolean(reader.readLine());

        if (!response) {
            System.err.println("Username already exist");
            return false;
        }

        System.out.println("welcome " + username);
        return true;

    }

    private void sendMessage(@NotNull Socket socket) throws IOException {

        @NotNull PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        @NotNull BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        @NotNull String msg = in.readLine();

        @NotNull Message message = new Message(OffsetDateTime.now(), msg);

        writer.println(msg);

        @NotNull String time = message.getTime().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"));

        System.out.println("[" + time + "] " + "You: " + message.getContent());

    }

    private void receiveMessage(@NotNull Socket socket) throws IOException {

        @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        @NotNull String msg = reader.readLine();

        if (msg.isEmpty()) {
            return;
        }

        System.out.println(msg);

    }

    public void start() throws IOException {
        @Nullable Socket client = getSocket();

        if (client != null && client.isConnected()) {
            throw new UnsupportedOperationException("Socket already active");
        }

        this.socket = new Socket(address.getHostName(), address.getPort());

        if (authentication(socket)) {
            while (socket.isConnected()) {
                receiveMessage(socket);
                sendMessage(socket);
            }
        }

    }

    public @Nullable Socket getSocket() {
        return socket;
    }
}
