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

                        try {
                            if (!key.isValid()) {
                                continue;
                            }

                            if (key.isAcceptable()) {
                                @NotNull SocketChannel channel = protocol.accept(key);
                                log.info("{} Trying to connect", channel.getLocalAddress());
                                @NotNull Client client = new Client(chat, channel);
                                chat.getClients().add(client);
                            }

                            if (key.isReadable() && key.isWritable()) {

                                @Nullable Client client = chat.getClient((SocketChannel) key.channel());
                                if (client == null)
                                    throw new SocketException();

                                // users authentication
                                if (!client.isAuthenticated()) {
                                    @Nullable String username = protocol.read(key);
                                    if (username == null)
                                        throw new SocketException();

                                    boolean isUserExist = chat.getUsers().stream().anyMatch(user -> user.getUsername().equals(username));

                                    if (!isUserExist) {
                                        protocol.write(false, key);
                                    } else {
                                        @NotNull User user = new User(username, client);
                                        chat.getUsers().add(user);
                                        client.setAuthenticated(true);
                                    }
                                }

                                // listen messages
                                @Nullable String msg = protocol.read(key);
                                if (msg == null)
                                    throw new SocketException();

                                @Nullable User user = client.getUser();
                                if (user == null)
                                    throw new SocketException();

                                @NotNull Message message = new Message(msg, user);
                                // send message for all
                                protocol.broadcast(message, chat, key);

                            }

                        } catch (SocketException | ClosedChannelException e) {
                            @NotNull SocketChannel channel = (SocketChannel) key.channel();
                            @Nullable Client c = chat.getClient(channel);
                            log.info("{} Disconnect", channel.getLocalAddress());
                            if (c != null) c.close();
                            if (c == null) channel.close();
                        }
                    }
                }

            } catch (IOException e) {
                log.error("A error occured: {}", e.getMessage());
            }
        }
    }

    // getters

    public @NotNull ChatServer getChat() {
        return chat;
    }

}
