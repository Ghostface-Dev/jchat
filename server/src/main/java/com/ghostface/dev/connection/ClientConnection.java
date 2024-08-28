package com.ghostface.dev.connection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ClientConnection {

    private final @NotNull SocketChannel channel;
    private final @NotNull ServerSystem system;

    public ClientConnection(@NotNull SocketChannel channel, @NotNull ServerSystem system) {
        this.channel = channel;
        this.system = system;
    }

    public void close() throws IOException {
        channel.close();
        system.getClients().remove(this);
    }

    public @Nullable String read(@NotNull SelectionKey key) throws IOException {
        @NotNull ByteBuffer buffer = ByteBuffer.allocate(4096);
        @NotNull StringBuilder builder = new StringBuilder();
        buffer.clear();

        @Range(from = 0, to = Integer.MAX_VALUE)
        int response = channel.read(buffer);

        if (response == -1) {
            throw new SocketException();
        } else if (response == 0) {
            return null;
        } else while (response > 0) {
            buffer.flip();
            builder.append(StandardCharsets.UTF_8.decode(buffer));
            buffer.clear();

            response = channel.read(buffer);
        }

        return builder.toString();
    }

    // Getters

    public @NotNull SocketChannel getChannel() {
        return channel;
    }

    public @NotNull ServerSystem getSystem() {
        return system;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull ClientConnection that = (ClientConnection) object;
        return Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(channel);
    }
}
