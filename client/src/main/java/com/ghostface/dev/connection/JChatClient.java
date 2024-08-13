package com.ghostface.dev.connection;

import com.ghostface.dev.impl.MessageImpl;
import com.ghostface.dev.impl.ScreeningThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;


public final class JChatClient {

    private final @NotNull InetSocketAddress address;
    private @Nullable Socket socket;

    public JChatClient(@NotNull InetSocketAddress address) {
        this.address = address;
    }

    private void sendMessage(final @NotNull Socket socket) {
        try {
            @NotNull PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            @NotNull BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            @NotNull String msg = input.readLine();

            writer.println(msg);


        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void readMessage(@NotNull Socket socket) {
        try {
            @NotNull BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println(reader.readLine());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void join() throws Exception {
        @Nullable Socket socket = getSocket();

        if (socket != null && socket.isConnected()) {
            throw new IllegalArgumentException("Socket already is running");
        }

        this.socket = new Socket(address.getHostName(), address.getPort());
        System.out.println("joined in the server");

        @NotNull Thread screening = new ScreeningThread(this.socket);
        screening.start();
        screening.join();

        while (this.socket.isBound()) {
            sendMessage(this.socket);
            readMessage(this.socket);
        }

    }

    // getters

    public @Nullable Socket getSocket() {
        return socket;
    }

    public @NotNull InetSocketAddress getAddress() {
        return address;
    }

}
