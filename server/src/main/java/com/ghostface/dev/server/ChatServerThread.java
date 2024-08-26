package com.ghostface.dev.server;

import com.ghostface.dev.connection.Client;
import com.ghostface.dev.entity.Message;
import com.ghostface.dev.entity.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.net.SocketException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Optional;


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
            @NotNull Iterator<@NotNull SelectionKey> keyIterator;

            try {
                int ready = selector.select();
                if (ready == 0) continue;
                keyIterator = selector.selectedKeys().iterator();
            } catch (ClosedChannelException e) {
                break;
            } catch (IOException e) {
                continue;
            }

            while (keyIterator.hasNext()) {
                @NotNull SelectionKey key = keyIterator.next();
                keyIterator.remove();

                try {
                    if (key.isAcceptable()) {
                        @NotNull SocketChannel channel = server.accept();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                        @NotNull Client client = new Client(chat, channel);
                        chat.getClients().add(client);
                    }

                    if (key.isReadable()) {
                        @NotNull Optional<@NotNull Client> optionalClient = chat.getClient((SocketChannel) key.channel());

                        if (!optionalClient.isPresent()) throw new ClosedChannelException();

                        @NotNull Client client = optionalClient.get();

                        if (!client.isAuthenticated()) {
                            @Nullable String username = client.read(key);
                            int attempts = 0;

                            while (username == null && attempts < 5) {
                                username = client.read(key);
                                attempts++;
                            }

                            if (username == null) {
                                client.close();
                                break;
                            }

                            @NotNull User user = new User(username, client);

                            if (chat.getUsers().contains(user)) {
                                client.write(false);
                                log.info("Failed authentication of {}", client.getChannel().getLocalAddress());
                                client.close();
                            } else {
                                chat.getUsers().add(user);
                                client.setAuthenticated(true);
                                chat.broadcast(user.getUsername() + "Joined");
                                log.info("Success authentication of {}", client.getChannel().getLocalAddress());
                            }

                        } else while (client.getChannel().isOpen()) {
                            @Nullable String content = client.read(key);
                            @NotNull Optional<@NotNull User> optional = client.getUser();

                            if (!optional.isPresent()) {
                                client.close();
                                break;
                            }

                            if (content != null) {
                                @NotNull Message msg = new Message(content, optional.get());
                                chat.broadcast(msg);
                            }

                        }

                    }

                } catch (ClosedChannelException e) {
                    @NotNull SocketChannel channel = (SocketChannel) key.channel();
                    try {
                        channel.close();
                    } catch (IOException ignore) {}
                } catch (IOException e) {
                    log.error(e.getMessage());
                }

            }

        }

    }

    private void closeClient(@NotNull SelectionKey key) {
        @NotNull Optional<@NotNull Client> optionalClient = chat.getClient((SocketChannel) key.channel());
        try {
            if (!optionalClient.isPresent()) {
                @NotNull SocketChannel channel = (SocketChannel) key.channel();
                log.info("{} Disconnect", channel.getLocalAddress());
                channel.close();
            } else {
                optionalClient.get().close();
            }
        } catch (IOException ignore) {}
    }

    // getters

    public @NotNull ChatServer getChat() {
        return chat;
    }

}
