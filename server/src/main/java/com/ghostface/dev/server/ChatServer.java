package com.ghostface.dev.server;

import com.ghostface.dev.connection.Client;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ChatServer {

    private final @NotNull Set<@NotNull Client> clients = ConcurrentHashMap.newKeySet();
    private @Nullable ServerSocket socket;
    private @Nullable Selector selector;
    private @Nullable Thread thread;

    public synchronized boolean start(@NotNull InetSocketAddress address) throws IOException {
        @Nullable ServerSocket socket = getSocket();
        @Nullable Selector selector = getSelector();

        if ((socket != null && socket.isBound()) || selector != null ) {
            return false;
        }

        this.selector = Selector.open();
        @NotNull ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.register(this.selector, SelectionKey.OP_ACCEPT);
        channel.bind(address);

        this.socket = channel.socket();

        this.thread = new ChatServerThread(this);
        this.thread.start();

        return true;

    }

    public synchronized boolean stop() throws IOException {
        @Nullable ServerSocket socket = getSocket();

        if ((socket == null || !socket.isBound()) || getSelector() == null || this.thread == null) {
            return false;
        }

        for (@NotNull Client client : clients) {
            client.getSocket().close();
        }

        this.thread.interrupt();

        this.socket.close();
        this.selector.close();
        clients.clear();

        this.selector = null;
        this.socket = null;
        this.thread = null;

        return true;
    }

    public @NotNull Set<@NotNull Client> getClients() {
        return clients;
    }

    public @Nullable ServerSocket getSocket() {
        return socket;
    }

    public @Nullable Selector getSelector() {
        return selector;
    }

}
