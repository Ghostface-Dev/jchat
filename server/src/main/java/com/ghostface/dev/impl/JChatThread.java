package com.ghostface.dev.impl;

import com.ghostface.dev.JChat;
import com.ghostface.dev.connection.JChatClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public final class JChatThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(JChatThread.class);

    private final @NotNull JChat chat;
    private final @NotNull ServerSocket server;
    private @Nullable Socket client;
    private final @NotNull Selector selector;

    public JChatThread(@NotNull JChat chat) {
        this.chat = chat;

        @Nullable ServerSocket socket = getChat().getSocket();
        @Nullable Selector selector = getChat().getSelector();

        if (socket == null || selector == null) {
            throw new IllegalArgumentException("The chat is not active");
        }

        this.server = socket;
        this.selector = selector;
    }

    // methods

    private boolean acceptConnection(@NotNull SelectionKey key) throws IOException {

        if (!key.isAcceptable()) {
            return false;
        } else {
            @NotNull SocketChannel channel = server.accept().getChannel();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);

            this.client = channel.socket();

            InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();
            log.warn("{}:{} Trying to connect", address.getHostName(), address.getPort());
            return true;
        }

    }


    @Override
    public void run() {
        while (server.isBound() && selector.isOpen()) {
            try {
                int channels = selector.select();
                if (channels == 0) continue;

                @NotNull Set<@NotNull SelectionKey> keySet = selector.selectedKeys();
                @NotNull Iterator<@NotNull SelectionKey> keyIterator = keySet.iterator();

                while (keyIterator.hasNext()) {
                    @NotNull SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {

                        if (!acceptConnection(key)) {
                            throw new IllegalArgumentException("The Key of [acceptConnection] method is not ready to accept");
                        } else {
                            assert this.client != null : "The Socket Client is null";
                            @NotNull JChatClient client = new JChatClient(chat, this.client);
                            // client is already in client set
                        }

                    } else if (key.isReadable()) {
                        
                    }

                }

            } catch (IOException e) {
                log.atError().setCause(e).log("Cannot select key: {}", e.getMessage());
            }
        }
    }


    // Getters

    public @NotNull JChat getChat() {
        return chat;
    }

    public @NotNull ServerSocket getServer() {
        return server;
    }

    public @NotNull Selector getSelector() {
        return selector;
    }

}
