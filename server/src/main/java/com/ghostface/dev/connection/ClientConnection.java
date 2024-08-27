package com.ghostface.dev.connection;

import com.ghostface.dev.entity.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.channels.SocketChannel;
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
