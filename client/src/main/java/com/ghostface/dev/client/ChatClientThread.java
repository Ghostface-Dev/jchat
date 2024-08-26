package com.ghostface.dev.client;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

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

    }

    public @NotNull ChatClient getClient() {
        return client;
    }
}
