package com.ghostface.dev;

import com.ghostface.dev.entity.User;
import com.ghostface.dev.impl.JChatServerThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class JChatServer {

    private final @NotNull Map<@NotNull Socket,@NotNull User> users = new HashMap<>();
    private final @NotNull InetSocketAddress address;

    private @Nullable ServerSocket server;
    private @Nullable Thread thread;

    public JChatServer(@NotNull InetSocketAddress address) {
        this.address = address;
    }

    // getters

    public @Nullable Thread getThread() {
        return thread;
    }

    public @Nullable ServerSocket getServer() {
        return server;
    }

    public @NotNull InetSocketAddress getAddress() {
        return address;
    }

    public @NotNull Map<@NotNull Socket, @NotNull User> getUsers() {
        return users;
    }

    // loaders

    public synchronized void start() throws IOException {
        @Nullable ServerSocket socket = getServer();

        if (socket != null && socket.isBound()) {
            throw new UnsupportedOperationException("Server already is running");
        }

        this.server = new ServerSocket(address.getPort(), 50, address.getAddress());;

        this.thread = new JChatServerThread(this);
        this.thread.start();
    }

    public synchronized void stop() {}


}
