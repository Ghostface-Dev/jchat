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
import java.time.Duration;
import java.time.Instant;
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
                int readyChannels = selector.select();
                if (readyChannels == 0) continue;
                keyIterator = selector.selectedKeys().iterator();
            } catch (ClosedSelectorException e) {
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

                        if (!client.isAuthenticated()) {
                            @Nullable String username = client.read(key);

                            while (username == null) {
                                username = client.read(key);
                            }

                            @NotNull String finalUsername = username;
                            boolean isUserExist = chat.getUsers().stream().anyMatch(user -> user.getUsername().equals(finalUsername));

                            if (isUserExist) {
                                client.write(false);
                                throw new SocketException("Failed to authenticate");
                            } else {
                                @NotNull User user = new User(finalUsername, client);
                                chat.getClients().add(client);
                                chat.getUsers().add(user);
                                client.write(true);
                            }

                        }
                    }

                    if (key.isReadable()) {
                        @NotNull Optional<@NotNull Client> optionalClient = chat.getClient((SocketChannel) key.channel());
                        @NotNull Optional<@NotNull User> optionalUser = chat.getUser((SocketChannel) key.channel());

                        while (optionalClient.isPresent() && optionalUser.isPresent()) {
                            @Nullable String message = optionalClient.get().read(key);
                            if (message != null) {
                                @NotNull Message msg = new Message(message, optionalUser.get());
                                chat.broadcast(msg);
                            }
                        }

                        throw new SocketException("Connection lost while read client message");
                    }
                } catch (SocketException | ClosedChannelException e) {
                    log.error(e.getMessage());
                    closeClient(key);
                } catch (IOException ignore) {
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
