package com.ghostface.dev.connection;

import com.ghostface.dev.entity.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ServerSystem {

    private final @NotNull Set<@NotNull ClientConnection> clients = ConcurrentHashMap.newKeySet();

    private final @NotNull InetSocketAddress address;
    private @Nullable Selector selector;
    private @Nullable ServerSocketChannel channel;

    private final @NotNull Thread thread = new ServerSystemThread();

    public ServerSystem(@NotNull InetSocketAddress address) {
        this.address = address;
    }

    public boolean start() throws IOException {
        if (getChannel() != null || getSelector() != null) return false;

        selector = Selector.open();

        channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.register(this.selector, SelectionKey.OP_ACCEPT);
        channel.bind(address);

        thread.start();

        return true;
    }

    public boolean close() throws IOException {

        if (getChannel() == null || !getChannel().socket().isBound() || getSelector() == null || !thread.isAlive()) {
            return false;
        }

        thread.interrupt();

        for (@NotNull ClientConnection connection: clients) {
            connection.close();
        }

        channel.close();
        selector.close();

        clients.clear();

        channel = null;
        selector = null;

        return true;
    }

    // Getters

    public @NotNull Set<@NotNull ClientConnection> getClients() {
        return clients;
    }

    public @Nullable Selector getSelector() {
        return selector;
    }

    public @Nullable ServerSocketChannel getChannel() {
        return channel;
    }
}
