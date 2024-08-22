package com.ghostface.dev.server;

import com.ghostface.dev.connection.Client;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ChatServer {

    private final @NotNull Set<@NotNull Client> clients = ConcurrentHashMap.newKeySet();
    private @Nullable ServerSocketChannel channel;
    private @Nullable Selector selector;
    private @Nullable Thread thread;

    public synchronized boolean start(@NotNull InetSocketAddress address) throws IOException {
        @Nullable ServerSocketChannel channel = getSocket();
        @Nullable Selector selector = getSelector();

        if ((channel != null && channel.socket().isBound()) || selector != null ) {
            return false;
        }

        this.selector = Selector.open();

        this.channel = ServerSocketChannel.open();
        this.channel.configureBlocking(false);
        this.channel.register(this.selector, SelectionKey.OP_ACCEPT);
        this.channel.bind(address);

        this.thread = new ChatServerThread(this);
        this.thread.start();

        return true;
    }

    public synchronized boolean stop() throws IOException {

        if ((channel == null || !channel.socket().isBound()) || getSelector() == null || this.thread == null) {
            return false;
        }

        for (@NotNull Client client : clients) {
            client.getChannel().close();
        }

        this.thread.interrupt();

        this.channel.close();
        this.selector.close();
        clients.clear();

        this.selector = null;
        this.channel = null;
        this.thread = null;

        return true;
    }

    public @NotNull Set<@NotNull Client> getClients() {
        return clients;
    }

    public @Nullable ServerSocketChannel getSocket() {
        return channel;
    }

    public @Nullable Selector getSelector() {
        return selector;
    }

}
