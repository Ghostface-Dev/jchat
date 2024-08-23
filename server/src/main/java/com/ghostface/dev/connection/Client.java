package com.ghostface.dev.connection;

import com.ghostface.dev.entity.User;
import com.ghostface.dev.server.ChatServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public final class Client {

    private final @NotNull ChatServer chat;
    private final @NotNull SocketChannel channel;
    private @Nullable User user;
    private boolean authenticated = false;

    public Client(@NotNull ChatServer chat, @NotNull SocketChannel channel) {
        this.chat = chat;

        if (!channel.socket().isConnected()) throw new IllegalArgumentException("Channel is not active");

        this.channel = channel;
        chat.getClients().add(this);
    }

    public void close() throws ClosedChannelException {
        chat.getClients().remove(this);
        try {
            channel.close();
        } catch (IOException e) {
            throw new ClosedChannelException();
        }
    }

    // getters

    public @NotNull ChatServer getChat() {
        return chat;
    }

    public @NotNull SocketChannel getChannel() {
        return channel;
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

    // natives

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Client client = (Client) object;
        return Objects.equals(channel, client.channel) && Objects.equals(user, client.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, user);
    }
}
