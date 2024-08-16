package com.ghostface.dev.connection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public final class JChat {

    private final @NotNull Collection<@NotNull JChatClient> clients = new JChatClients();
    private final @NotNull InetSocketAddress address;

    private @Nullable ServerSocket socket;
    private @Nullable Selector selector;
    private @Nullable Thread thread;

    public JChat(@NotNull InetSocketAddress address) {
        this.address = address;
    }

    // loaders

    public synchronized boolean start() throws IOException {

        @Nullable ServerSocket socket = getSocket();

        if ((socket != null && socket.isBound()) || (selector != null && selector.isOpen())) {
            return false;
        }

        this.selector = Selector.open();

        @NotNull ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        this.socket = channel.socket();

        channel.socket().bind(address);
        channel.register(getSelector(), SelectionKey.OP_ACCEPT);

        return true;
    }

    // Getters


    public @Nullable ServerSocket getSocket() {
        return socket;
    }

    public @Nullable Selector getSelector() {
        return selector;
    }

    public @Nullable Thread getThread() {
        return thread;
    }

    public @NotNull Collection<@NotNull JChatClient> getClients() {
        return clients;
    }
}
