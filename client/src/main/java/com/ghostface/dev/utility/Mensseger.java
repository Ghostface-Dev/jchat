package com.ghostface.dev.utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public final class Mensseger {

    public static void write(@NotNull SocketChannel channel, @NotNull String s) throws IOException {
        channel.write(ByteBuffer.wrap(s.getBytes()));
    }

}
