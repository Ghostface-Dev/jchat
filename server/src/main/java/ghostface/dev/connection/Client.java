package ghostface.dev.connection;

import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import ghostface.dev.exception.AuthenticationException;
import ghostface.dev.management.DataBase;
import ghostface.dev.packet.ConnectionPacket;
import ghostface.dev.packet.ConnectionPacket.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class Client {

    private final @NotNull DataBase dataBase;
    private final @NotNull SocketChannel channel;

    public Client(@NotNull DataBase dataBase, @NotNull SocketChannel channel) {
        this.dataBase = dataBase;
        this.channel = channel;
    }

    public void authenticate(@NotNull Email email, @NotNull Password password) throws AuthenticationException {
        @NotNull CompletableFuture<@NotNull ConnectionPacket> future = dataBase.authenticate(email, password, this);
        if (future.isCompletedExceptionally()) {
            throw new AuthenticationException(future.exceptionNow().getMessage());
        }
    }

    // TODO close client

    public @Nullable String read(@NotNull SelectionKey key) throws ClosedChannelException {
        @NotNull SocketChannel channel = (SocketChannel) key.channel();

        @NotNull ByteBuffer buffer = ByteBuffer.allocate(4096); // 4KB
        @NotNull StringBuilder builder = new StringBuilder();
        buffer.clear();

        try {
            @Range(from = 0, to = Integer.MAX_VALUE)
            int response = channel.read(buffer);

            if (response == -1) {
                throw new IOException();
            } else if (response == 0) {
                return null;
            } else while (response > 0) {
                buffer.flip();
                builder.append(StandardCharsets.UTF_8.decode(buffer));
                buffer.clear();
                response = channel.read(buffer);
            }
            return builder.toString();
        } catch (IOException e) {
            throw new ClosedChannelException();
        }
    }

    public void write(@NotNull String string) throws IOException {
        channel.write(ByteBuffer.wrap(string.getBytes()));
    }

    public void broadcast(@NotNull String string) throws IOException {
        for (@NotNull Client client : dataBase.getClients()) {
            if (!client.equals(this)) {
                channel.write(ByteBuffer.wrap(string.getBytes()));
            }
        }
    }

    // Getters

    public @NotNull Optional<@NotNull Client> getClient(@NotNull SocketChannel channel) {
        return dataBase.getClients().stream().filter(client -> client.getChannel().equals(channel)).findFirst();
    }

    public @NotNull SocketChannel getChannel() {
        return channel;
    }

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
