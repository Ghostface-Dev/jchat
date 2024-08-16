package com.ghostface.dev.connection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public final class JChatClient {

    private final @NotNull JChat chat;
    private final @NotNull SocketChannel channel;
    private final @NotNull InetSocketAddress address;
    private boolean authenticated;

    public JChatClient(@NotNull JChat chat, @NotNull SocketChannel channel) {
        this.chat = chat;
        this.channel = channel;
        this.address = new InetSocketAddress(channel.socket().getInetAddress(), channel.socket().getPort());
        this.authenticated = false;
    }

    // methods


    // getters

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public @NotNull SocketChannel getChannel() {
        return channel;
    }

    public @NotNull InetSocketAddress getAddress() {
        return address;
    }

    @NotNull JChat getChat() {
        return chat;
    }

}
