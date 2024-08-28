package com.ghostface.dev.connection;

import com.ghostface.dev.entity.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ServerSystem {

    private final @NotNull Map<@NotNull User, @NotNull String> users = new HashMap<>();
    private final @NotNull Set<@NotNull ClientConnection> clients = ConcurrentHashMap.newKeySet();

    private final @NotNull InetSocketAddress address;
    private final @NotNull Thread thread = new ServerSystemThread(this);
    private @Nullable Selector selector;
    private @Nullable ServerSocketChannel channel;

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

    public @NotNull Optional<@NotNull ClientConnection> getConnection(@NotNull SocketChannel channel) {
        return clients.stream().filter(client -> client.getChannel().equals(channel)).findFirst();
    }

    public @NotNull Optional<@NotNull User> getUser(@NotNull SocketChannel channel) {
        return users.keySet().stream().filter(user -> user.getConnection().getChannel().equals(channel)).findFirst();
    }

//    public @NotNull CompletableFuture<@NotNull Boolean> register(@NotNull String username, @NotNull String password) {
//
//        return CompletableFuture.supplyAsync(() -> {
//
//            Optional<@NotNull User> optionalUser = users.keySet().stream().filter(user -> user.getUsername().equals(username)).findFirst();
//
//            return !optionalUser.isPresent();
//
//        });
//
//    }

    // Getters

    public @NotNull Set<@NotNull ClientConnection> getClients() {
        return clients;
    }

    public @NotNull Map<@NotNull User, @NotNull String> getUsers() {
        return users;
    }

    public @Nullable Selector getSelector() {
        return selector;
    }

    public @Nullable ServerSocketChannel getChannel() {
        return channel;
    }



}
