package ghostface.dev.server;

import ghostface.dev.management.DataBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public final class ServerJChat {

    private final @NotNull DataBase dataBase;
    private final @NotNull InetSocketAddress address;
    private @Nullable ServerSocketChannel channel;
    private @Nullable Selector selector;
    private @Nullable Thread thread;

    public ServerJChat(@NotNull InetSocketAddress address) {
        this.address = address;
        this.dataBase = DataBase.getInstance();
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

        this.channel = null;
        this.selector = null;
        this.thread = null;

        return true;
    }

    // Getters

    public @NotNull DataBase getDataBase() {
        return dataBase;
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
