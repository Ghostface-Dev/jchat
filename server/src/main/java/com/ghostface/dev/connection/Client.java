package com.ghostface.dev.connection;

import com.ghostface.dev.entity.User;
import com.ghostface.dev.server.ChatServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public final class Client {

    private static final @NotNull Logger log = LoggerFactory.getLogger(Client.class);

    private final @NotNull ChatServer chat;
    private final @NotNull SocketChannel channel;

    private boolean authenticated = false;

    public Client(@NotNull ChatServer chat, @NotNull SocketChannel channel) {
        this.chat = chat;

        if (!channel.socket().isConnected()) throw new IllegalArgumentException("Channel is not active");

        this.channel = channel;
    }

    public void close() {
        @Nullable User user = chat.getUsers().stream().filter(user1 -> user1.getClient().equals(this)).findFirst().orElse(null);
        try {
            log.info("{} Disconect", channel.getLocalAddress());
            channel.close();
            if (user != null) chat.getUsers().remove(user);
        } catch (IOException ignore) {}

        chat.getClients().remove(this);
    }

    public @Nullable String read(@NotNull SelectionKey key) throws IOException {
        @NotNull ByteBuffer buffer = ByteBuffer.allocate(4096);
        @NotNull StringBuilder builder = new StringBuilder();

        buffer.clear();
        long response = channel.read(buffer);

        if (response == -1) {
            throw new ClosedChannelException();
        }
        if (response == 0) {
            return null;
        }
        while (response > 0) {
            buffer.flip();
            builder.append(StandardCharsets.UTF_8.decode(buffer));
            buffer.clear();
            response = channel.read(buffer);
        }
        return builder.toString();
    }

    public void write(@NotNull String s) throws ClosedChannelException {
        try {
            channel.write(ByteBuffer.wrap(s.getBytes()));
        } catch (IOException e) {
            throw new ClosedChannelException();
        }
    }

    public void write(boolean value) throws ClosedChannelException {
        try {
            channel.write(ByteBuffer.wrap(String.valueOf(value).getBytes()));
        } catch (IOException e) {
            throw new ClosedChannelException();
        }
    }

    // getters

    public @NotNull Optional<@NotNull User> getUser() {
        return chat.getUsers().stream().filter(user1 -> user1.getClient().equals(this)).findFirst();
    }

    public @NotNull ChatServer getChat() {
        return chat;
    }



    public @NotNull SocketChannel getChannel() {
        return channel;
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
        return Objects.equals(channel, client.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(channel);
    }
}
