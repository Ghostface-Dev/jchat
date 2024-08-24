package com.ghostface.dev.utility;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public final class Mensseger {

    public static @Nullable String read(@NotNull SocketChannel channel) throws IOException {
        @NotNull ByteBuffer buffer = ByteBuffer.allocate(4096);
        @NotNull StringBuilder builder = new StringBuilder();
        buffer.clear();

        int response = channel.read(buffer);

        if (response == -1)
            throw new ClosedChannelException();

        if (response == 0)
            return null;

        while (response > 0) {
            buffer.flip();
            builder.append(StandardCharsets.UTF_8.decode(buffer));
            buffer.clear();
            response = channel.read(buffer);
        }

        return builder.toString();
    }

    public static void write (@NotNull String s ,@NotNull SocketChannel channel) throws SocketException {
        try {
            channel.write(ByteBuffer.wrap(s.getBytes()));
        } catch (IOException e) {
            throw new SocketException();
        }
    }

}
