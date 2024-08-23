package com.ghostface.dev.server;

import com.ghostface.dev.connection.Client;
import com.ghostface.dev.entity.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

final class HandlerProtocol {

    private final int bufSize;

    public HandlerProtocol (int bufSize) {
        this.bufSize = bufSize;
    }

    public @NotNull SocketChannel accept(@NotNull SelectionKey key) throws IOException {
        @NotNull SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        channel.configureBlocking(false);
        channel.register(key.selector(), SelectionKey.OP_READ | SelectionKey.OP_WRITE, ByteBuffer.allocate(bufSize));

        return channel;
    }

    public @Nullable String read(@NotNull SelectionKey key) throws IOException {
        @NotNull SocketChannel channel = (SocketChannel) key.channel();
        @NotNull ByteBuffer buffer = ByteBuffer.allocate(bufSize);
        @NotNull StringBuilder builder = new StringBuilder();

        buffer.clear();
        long response = channel.read(buffer);

        if (response == -1) {
            throw new SocketException();
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

    public void write(@NotNull String s, @NotNull SelectionKey key) throws ClosedChannelException {
        try {
            @NotNull SocketChannel channel = (SocketChannel) key.channel();
            channel.write(ByteBuffer.wrap(s.getBytes()));
        } catch (IOException e) {
            throw new ClosedChannelException();
        }
    }

    public void write(boolean value, @NotNull SelectionKey key) throws ClosedChannelException {
        try {
            @NotNull SocketChannel channel = (SocketChannel) key.channel();
            channel.write(ByteBuffer.wrap(String.valueOf(value).getBytes()));
        } catch (IOException e) {
            throw new ClosedChannelException();
        }
    }

    public void broadcast(@NotNull Message msg, @NotNull ChatServer chatServer ,@NotNull SelectionKey key) throws ClosedChannelException {
        @NotNull String time = msg.getTime().format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"));
        @NotNull String format = "[" + time + "] " + msg.getUser().getUsername() + ": " + msg.getContent();

        for (@NotNull Client client : chatServer.getClients()) {
            try {
                client.getChannel().write(ByteBuffer.wrap(format.getBytes()));
            } catch (IOException e) {
                throw new ClosedChannelException();
            }
        }
    }

}
