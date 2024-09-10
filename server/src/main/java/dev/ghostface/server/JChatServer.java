package dev.ghostface.server;

import dev.ghostface.connection.Client;
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
import java.security.Key;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class JChatServer {

    private final @NotNull Set<@NotNull Client> clients = ConcurrentHashMap.newKeySet();
    private final @NotNull InetSocketAddress address;

    private @Nullable ServerSocket channel;
    private @Nullable Selector selector;
    private @Nullable Thread thread;

    public JChatServer(@NotNull InetSocketAddress address) {
        this.address = address;
    }

    public synchronized boolean run() throws IOException {
        if (getChannel() != null && getChannel().isBound() || getSelector() != null) {
            return false;
        }

        this.selector = Selector.open();
        @NotNull ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
        channel.bind(address);
        this.channel = channel.socket();

        this.thread = new JChatServerThread(this);
        this.thread.start();

        return true;
    }

    // Getters

    public @NotNull Optional<@NotNull Client> getClient(@NotNull SelectionKey key) {
        @NotNull SocketChannel channel = (SocketChannel) key.channel();
        return clients.stream().filter(client -> client.getSocket().equals(channel.socket())).findFirst();
    }

    public @NotNull Set<@NotNull Client> getClients() {
        return clients;
    }

    public @NotNull InetSocketAddress getAddress() {
        return address;
    }

    public @Nullable ServerSocket getChannel() {
        return channel;
    }

    public @Nullable Selector getSelector() {
        return selector;
    }
}
