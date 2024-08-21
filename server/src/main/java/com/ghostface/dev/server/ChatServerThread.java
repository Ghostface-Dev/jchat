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
    private final @NotNull ServerSocket server;
    private final @NotNull Selector selector;

    public ChatServerThread(@NotNull ChatServer chat) {
        this.chat = chat;
        @Nullable ServerSocket socket = getChat().getSocket();
        @Nullable Selector selector = getChat().getSelector();

        if ((socket == null || !socket.isBound()) || selector == null) {
            throw new IllegalArgumentException("Chat is not active");
        }

        this.server = socket;
        this.selector = selector;
    }

    @Override
    public void run() {
        log.info("Server is running on {} port", server.getLocalPort());
        while (server.isBound() && selector.isOpen()) {
            @Nullable SocketChannel channel = null;
            @Nullable Iterator<@NotNull SelectionKey> keyIterator = null;

            try {
                int readyChannel = selector.select();

                if (readyChannel > 0) {
                    keyIterator = selector.selectedKeys().iterator();

                    while (keyIterator.hasNext()) {
                        @NotNull SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        // configure and accept the socket
                        if (key.isAcceptable()) {
                            channel = server.accept().getChannel();
                            channel.configureBlocking(false);
                            channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

                            log.warn("{} Trying to connect", channel.socket().getInetAddress().getHostName());

                            @NotNull Client client = new Client(chat, channel);
                            // client already in CLIENTS by constructor
                        }

                        if (key.isReadable()) {
                            @NotNull SocketChannel clientChannel = (SocketChannel) key.channel();
                            @Nullable Client client = chat.getClients().stream().filter(c -> c.getChannel().equals(clientChannel)).findFirst().orElse(null);

                            if (client == null) {
                                clientChannel.close();
                            } else {
                                // todo authenticate

                                // waiting for message
                                @NotNull ClientHandler handler = new ClientHandler(client);
                                @Nullable String content = handler.read();

                                if (content == null) {
                                    log.info("Client message: null");
                                } else {
                                    log.info("Client Message: {}", content);
                                }

                            }

                        }

                    }

                }


            } catch (SocketException | ClosedChannelException e) {
                // close client (remove from clients)
                @Nullable SocketChannel finalChannel = channel;
                @Nullable Client client = chat.getClients().stream().filter(c -> c.getChannel().equals(finalChannel)).findFirst().orElse(null);

                if (client != null) try {
                    client.close();
                    log.atInfo().setCause(e).log("{} Disconect", channel.socket().getLocalAddress().getHostName());
                } catch (IOException ignore) {}

            } catch (ClosedSelectorException ignore) {

            } catch (IOException e) {
                log.atError().setCause(e).log("I/O error: Failed in connection: {}", e.getMessage());
            }
        }
    }

    // getters

    public @NotNull ChatServer getChat() {
        return chat;
    }

}
