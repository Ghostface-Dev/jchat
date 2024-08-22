package com.ghostface.dev.server;

import com.ghostface.dev.connection.Client;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.channels.*;
import java.util.Iterator;


final class ChatServerThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ChatServerThread.class);

    private final @NotNull ChatServer chat;
    private final @NotNull ServerSocketChannel server;
    private final @NotNull Selector selector;

    public ChatServerThread(@NotNull ChatServer chat) {
        this.chat = chat;
        @Nullable ServerSocketChannel socket = getChat().getSocket();
        @Nullable Selector selector = getChat().getSelector();

        if ((socket == null || !socket.socket().isBound()) || selector == null) {
            throw new IllegalArgumentException("Chat is not active");
        }

        this.server = socket;
        this.selector = selector;
    }

    @Override
    public void run() {

        try {
            log.info("Server is running on {} port", server.getLocalAddress());
        } catch (IOException e) {
            log.error("Cannot get local adress: {}", e.getMessage());
        }

        while (server.socket().isBound() && selector.isOpen()) {

            try {

                if (selector.select() > 0) {
                    @NotNull Iterator<@NotNull SelectionKey> keyIterator = selector.selectedKeys().iterator();

                    while (keyIterator.hasNext()) {
                        @NotNull HandlerProtocol protocol = new HandlerProtocol(4096); // 4kb
                        @NotNull SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isAcceptable()) {
                            @NotNull SocketChannel channel = protocol.accept(key);
                            log.info("{} Trying to connect", channel.getLocalAddress());
                            @NotNull Client client = new Client(chat, channel);
                            chat.getClients().add(client);
                        }

                        if (key.isReadable()) {
                            System.out.println(protocol.read(key));
                        }

                        if (key.isWritable()) {

                        }

                    }

                }
            } catch (SocketException ignore) {

            } catch (ClosedSelectorException | ClosedChannelException e) {
                log.atError().setCause(e).log("Thread cannot proceed because servers closed: {}", e.getMessage());
                try {
                    if (chat.stop()) log.atInfo().log("Thread has interrupt");
                } catch (IOException ignore) {}
            } catch (IOException e) {
                log.atError().setCause(e).log("I/O error: {}", e.getMessage());
                break;
            }

        }

    }

    // getters

    public @NotNull ChatServer getChat() {
        return chat;
    }

}
