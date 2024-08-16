package com.ghostface.dev.impl;

import com.ghostface.dev.connection.JChat;
import com.ghostface.dev.connection.JChatClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public final class JChatThread extends Thread {

    private final @NotNull JChat chat;
    private final @NotNull ServerSocket socket;
    private final @NotNull Selector selector;

    public JChatThread(@NotNull JChat chat) {
        this.chat = chat;

        @Nullable ServerSocket socket = getChat().getSocket();
        @Nullable Selector selector = getChat().getSelector();

        if (socket == null || selector == null) {
            throw new IllegalArgumentException("The chat server is not active");
        }

        this.socket = socket;
        this.selector = selector;
    }

    @Override
    public void run() {
        while (socket.isBound() && selector.isOpen()) {

            @NotNull Set<@NotNull SelectionKey> selectionKeys;
            @NotNull Iterator<@NotNull SelectionKey> keyIterator;

            try {
                @Range(from = 0, to = Long.MAX_VALUE)
                int channels = selector.select();
                if (channels == 0) continue;

                selectionKeys = selector.selectedKeys();
                keyIterator = selectionKeys.iterator();

            } catch (IOException e) {
                continue;
            }

            while (keyIterator.hasNext()) {
                @NotNull SelectionKey key = keyIterator.next();
                keyIterator.remove();

                // socket accept
                if (key.isAcceptable()) {
                    @Nullable SocketChannel clientChannel = null;

                    try {
                        clientChannel = socket.accept().getChannel();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        @NotNull JChatClient client = new JChatClient(getChat(), clientChannel);


                    } catch (IOException e) {

                    }
                }
            }
        }
    }


    // Getters

    public @NotNull JChat getChat() {
        return chat;
    }

    public @NotNull ServerSocket getSocket() {
        return socket;
    }

    public @NotNull Selector getSelector() {
        return selector;
    }
}
