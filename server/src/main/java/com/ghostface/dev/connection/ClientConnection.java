package com.ghostface.dev.connection;

import com.ghostface.dev.entity.User;
import com.ghostface.dev.server.ChatServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;

public final class ClientConnection {

    private final @NotNull ChatServer chat;
    private final @NotNull Socket socket;
    private @Nullable User user;
    private boolean authenticated = false;

    public ClientConnection(@NotNull ChatServer chat, @NotNull Socket socket) {
        this.chat = chat;
        this.socket = socket;
    }

    // methods

    public @Nullable String read() throws IOException {

        @NotNull ByteBuffer buffer = ByteBuffer.allocate(4096);
        @NotNull StringBuilder builder = new StringBuilder();
        buffer.clear();

        int response = socket.getChannel().read(buffer);

        if (response == -1) {
            throw new ClosedChannelException();
        } else if (response == 0) {
            return null;
        } else while (response > 0) {
            buffer.flip();
            builder.append(StandardCharsets.UTF_8.decode(buffer));
            buffer.clear();

            response = socket.getChannel().read(buffer);
        }

        return builder.toString();
    }

    public void close() throws IOException {
        // remove from users
        socket.close();
    }

    // getters

    public @NotNull ChatServer getChat() {
        return chat;
    }

    public @NotNull Socket getSocket() {
        return socket;
    }

    public @Nullable User getUser() {
        return user;
    }

    public void setUser(@Nullable User user) {
        if (this.user != null) {
            throw new IllegalArgumentException("User already defined");
        }
        this.user = user;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        if (this.authenticated) {
            throw new IllegalArgumentException("Client already authenticated");
        }
        this.authenticated = authenticated;
    }
}
