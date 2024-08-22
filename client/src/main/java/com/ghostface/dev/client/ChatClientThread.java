package com.ghostface.dev.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

final class ChatClientThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ChatClientThread.class);

    private final @NotNull ChatClient client;
    private final @NotNull SocketChannel channel;
    private final @NotNull Selector selector;

    public ChatClientThread(@NotNull ChatClient client) {
        this.client = client;

        @Nullable SocketChannel channel = client.getChannel();
        @Nullable Selector selector = client.getSelector();

        if (channel == null || !channel.isOpen()) {
            throw new IllegalArgumentException("Socket is not active");
        } else if (selector == null) {
            throw new IllegalArgumentException("Selector is null");
        }

        this.channel = channel;
        this.selector = selector;
    }

    @Override
    public void run() {

        while (selector.isOpen()) {

            try {

                if (selector.select() > 0) {
                    @NotNull Iterator<@NotNull SelectionKey> keyIterator = selector.selectedKeys().iterator();

                    while (keyIterator.hasNext()) {
                        @NotNull SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isConnectable()) {
                            if (channel.finishConnect()) {
                                channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, ByteBuffer.allocate(4096));
                                log.info("Connection {} is Succesfull", channel.getLocalAddress());
                            } else {
                                key.cancel();
                            }
                        }

                        if (key.isWritable()) {
                        }

                    }

                }



            } catch (IOException e) {
                log.error("I/O error: {}", e.getMessage());
                break;
            }

        }

    }

    public @NotNull ChatClient getClient() {
        return client;
    }
}
