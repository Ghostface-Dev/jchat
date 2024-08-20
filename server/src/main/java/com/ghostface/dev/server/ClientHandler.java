package com.ghostface.dev.server;

import com.ghostface.dev.connection.Client;
import com.ghostface.dev.entity.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;

final class ClientHandler {

    private final @NotNull Client client;

    public ClientHandler(@NotNull Client client) {
        this.client = client;
    }

    public @Nullable String read() throws IOException {
        @NotNull ByteBuffer buffer = ByteBuffer.allocate(4096);
        @NotNull StringBuilder builder = new StringBuilder();
        buffer.clear();

        int response = client.getSocket().getChannel().read(buffer);

        if (response == -1) {
            throw new ClosedChannelException();
        } else if (response == 0) {
            return null;
        } else while (response > 0) {
            buffer.flip();
            builder.append(StandardCharsets.UTF_8.decode(buffer));
            buffer.clear();

            response = client.getSocket().getChannel().read(buffer);
        }

        return builder.toString();
    }

    public void write(@NotNull String s) throws IOException {
        client.getSocket().getChannel().write(ByteBuffer.wrap(s.getBytes()));
    }

    public void write(boolean value) throws IOException {
        client.getSocket().getChannel().write(ByteBuffer.wrap(String.valueOf(value).getBytes()));
    }

    public void broadcastMessage(@NotNull Message msg) throws IOException {
        for (@NotNull Client clients : client.getChat().getClients()) {
            write(msg.toString());
        }
    }

    public @NotNull Client getClient() {
        return client;
    }
}
