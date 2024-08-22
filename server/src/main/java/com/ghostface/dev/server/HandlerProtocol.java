package com.ghostface.dev.server;

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

    public void write(@NotNull String s, @NotNull SelectionKey key) throws IOException {
        @NotNull SocketChannel channel = (SocketChannel) key.channel();

        channel.write(ByteBuffer.wrap(s.getBytes()));
    }

}
