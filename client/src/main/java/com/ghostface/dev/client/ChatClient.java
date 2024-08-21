package com.ghostface.dev.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public final class ChatClient {

    private @Nullable Selector selector;
    private @Nullable Socket socket;
    private @Nullable Thread thread;

    public synchronized boolean join(@NotNull InetSocketAddress address) throws IOException {

        if ((socket != null && socket.isBound()) || selector != null) {
            return false;
        }

        this.selector = Selector.open();

        @NotNull SocketChannel channel = SocketChannel.open();

        channel.configureBlocking(true);
        channel.register(this.selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);

        this.socket = channel.socket();
        this.socket.connect(address);

        this.thread = new ChatClientThread(this);
        this.thread.start();

        return true;
    }

    public synchronized boolean exit() throws IOException {

        if (selector == null || (socket == null || !socket.isBound()) || this.thread == null) {
            return false;
        }

        this.thread.interrupt();

        this.socket.close();
        this.selector.close();

        this.socket = null;
        this.selector = null;

        return true;
    }

    public @Nullable Selector getSelector() {
        return selector;
    }

    public @Nullable Socket getSocket() {
        return socket;
    }

    public @Nullable Thread getThread() {
        return thread;
    }
}
