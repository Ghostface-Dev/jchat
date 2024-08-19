package com.ghostface.dev;

import com.ghostface.dev.connection.JChatClient;
import com.ghostface.dev.impl.JChatThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.ServerSocket;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class JChat {

    private static final Logger log = LoggerFactory.getLogger(JChat.class);

    private final @NotNull Set<@NotNull JChatClient> clients = ConcurrentHashMap.newKeySet();
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
        @Nullable Selector key = getSelector();

        if ((socket != null && socket.isBound()) || (key != null && key.isOpen())) {
            return false;
        }

        this.selector = Selector.open();

        @NotNull ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        this.socket = channel.socket();

        channel.bind(address);
        channel.register(selector, SelectionKey.OP_ACCEPT);

        this.thread = new JChatThread(this);
        this.thread.start();

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

    public @NotNull Set<@NotNull JChatClient> getClients() {
        return clients;
    }

    public @NotNull InetSocketAddress getAddress() {
        return address;
    }
}
