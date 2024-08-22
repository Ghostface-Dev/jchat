package com.ghostface.dev.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public final class ChatClient {

    private @Nullable Selector selector;
    private @Nullable SocketChannel channel;
    private @Nullable Thread thread;

    public synchronized boolean join(@NotNull InetSocketAddress address) throws IOException {

        if ((channel != null && channel.socket().isBound()) || selector != null) {
            return false;
        }

        this.selector = Selector.open();

        channel = SocketChannel.open();

        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_CONNECT);
        channel.connect(address);


        this.thread = new ChatClientThread(this);
        this.thread.start();

        return true;
    }

    public synchronized boolean exit() throws IOException {

        if (selector == null || (channel == null || !channel.socket().isBound()) || this.thread == null) {
            return false;
        }

        this.thread.interrupt();

        this.channel.close();
        this.selector.close();

        this.channel = null;
        this.selector = null;

        return true;
    }

    public @Nullable Selector getSelector() {
        return selector;
    }

    public @Nullable SocketChannel getChannel() {
        return channel;
    }

    public @Nullable Thread getThread() {
        return thread;
    }
}
