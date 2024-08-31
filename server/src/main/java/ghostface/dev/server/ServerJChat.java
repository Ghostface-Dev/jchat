package ghostface.dev.server;

import ghostface.dev.Client;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ServerJChat {

    // todo add accounts

    private final @NotNull Set<@NotNull Client> clients = ConcurrentHashMap.newKeySet();
    private final @NotNull InetSocketAddress address;

    private @Nullable ServerSocketChannel channel;
    private @Nullable Selector selector;
    private @Nullable Thread thread;

    public ServerJChat(@NotNull InetSocketAddress address) {
        this.address = address;
    }

    public synchronized boolean start() throws IOException {
        @Nullable ServerSocketChannel channel = getChannel();
        @Nullable Selector selector = getSelector();
        @Nullable Thread thread = getThread();

        if (channel != null && channel.socket().isBound() || selector != null || thread != null) {
            return false;
        }

        this.selector = Selector.open();
        this.channel = ServerSocketChannel.open();
        this.channel.configureBlocking(false);
        this.channel.register(this.selector, SelectionKey.OP_ACCEPT);
        this.channel.bind(address);

        this.thread = new ServerThread(this);
        this.thread.start();

        return true;
    }

    public synchronized boolean stop() throws IOException {
        @Nullable ServerSocketChannel channel = getChannel();
        @Nullable Selector selector = getSelector();

        if ((channel == null || !channel.socket().isBound()) || selector == null || thread == null) {
            return false;
        }

        thread.interrupt();

        channel.close();
        selector.close();

        for (@NotNull Client client : clients) {
            client.close();
        }

        this.channel = null;
        this.selector = null;
        this.thread = null;
        clients.clear();

        return true;
    }

    public @NotNull Optional<@NotNull Client> getClient(@NotNull SelectionKey key) {
        @NotNull SocketChannel channel = (SocketChannel) key.channel();
        return clients.stream().filter(client -> client.getChannel().equals(channel)).findFirst();
    }

    // Getters

    public @NotNull Set<@NotNull Client> getClients() {
        return clients;
    }

    public @NotNull InetSocketAddress getAddress() {
        return address;
    }

    public @Nullable ServerSocketChannel getChannel() {
        return channel;
    }

    public @Nullable Selector getSelector() {
        return selector;
    }

    public @Nullable Thread getThread() {
        return thread;
    }
}
