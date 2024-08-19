package com.ghostface.dev.connection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public final class JChatRoom {

    private final @NotNull InetSocketAddress address;
    private @Nullable Selector selector;
    private @Nullable Socket socket;

    public JChatRoom(@NotNull InetSocketAddress address) {
        this.address = address;
    }

    public synchronized boolean join() throws IOException {

        @Nullable Selector selector = getSelector();
        @Nullable Socket socket = getSocket();

        if ((selector != null && selector.isOpen()) || (socket != null && socket.isBound())) {
            return false;
        }

        this.selector = selector;

        @NotNull SocketChannel channel = SocketChannel.open();

        channel.configureBlocking(false);

        this.socket = socket;

        channel.connect(address);
        channel.register(selector, SelectionKey.OP_CONNECT);

        return true;

    }

    // getters

    public @Nullable Selector getSelector() {
        return selector;
    }

    public @Nullable Socket getSocket() {
        return socket;
    }
}
