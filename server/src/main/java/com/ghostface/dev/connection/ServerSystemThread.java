package com.ghostface.dev.connection;

import com.ghostface.dev.entity.Message;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Optional;

final class ServerSystemThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ServerSystemThread.class);

    private final @NotNull ServerSystem system;
    private final @NotNull Selector selector;
    private final @NotNull ServerSocketChannel server;

    public ServerSystemThread(@NotNull ServerSystem system) {
        this.system = system;

        @Nullable Selector selector = system.getSelector();
        @Nullable ServerSocketChannel channel = system.getChannel();

        if (selector == null || (channel == null || !channel.socket().isBound()))
            throw new IllegalArgumentException("Server is not active");

        this.selector = selector;
        this.server = channel;
    }

    @Override
    public void run() {

        while (server.socket().isBound() && selector.isOpen()) {
            @NotNull Iterator<@NotNull SelectionKey> keyIterator;

            try {
                int ready = selector.select();
                if (ready == 0) continue;
                keyIterator = selector.selectedKeys().iterator();
            } catch (ClosedSelectorException e) {
                break;
            } catch (IOException e) {
                continue;
            }

            while (keyIterator.hasNext()) {
                @NotNull SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    try {
                        @NotNull SocketChannel channel = server.accept();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);

                        @NotNull ClientConnection connection = new ClientConnection(channel, system);
                        system.getClients().add(connection);

                        log.info("Accepted new client: {}", channel.getLocalAddress());
                    } catch (@NotNull Throwable throwable) {
                        log.error("Cannot accept connection: {}", throwable.getMessage());
                        continue;
                    }
                }

                if (key.isReadable()) {

                    try {

                        @NotNull Optional<@NotNull ClientConnection> optional = system.getConnection((SocketChannel) key.channel());

                        if (!optional.isPresent()) {
                            @NotNull SocketChannel channel = (SocketChannel) key.channel();
                            channel.close();
                        } else {
                            @NotNull ClientConnection connection = optional.get();

                            @Nullable String json = connection.read(key);

                            if (json != null) {
                                @NotNull JsonElement parseString = JsonParser.parseString(json);
                                @NotNull JsonObject object = parseString.getAsJsonObject();

                                if (!object.has("type"))
                                    throw new JsonIOException("Field 'type' is missing or incorrect");

                                @NotNull PacketConnection packet = PacketConnection.getInstance(object, connection.getChannel());

                                if (!packet.response(system)) {
                                    connection.close();
                                }

                            }

                        }
                    } catch (JsonIOException e) {
                        log.error("Packed error: {}", e.getMessage());
                        @NotNull SocketChannel channel = (SocketChannel) key.channel();
                        @NotNull Optional<@NotNull ClientConnection> optional = system.getConnection(channel);
                        try {
                            if (optional.isPresent()) {
                                optional.get().close();
                            } else {
                                channel.close();
                            }
                        } catch (IOException ignore) {
                        }

                    } catch (ClosedChannelException | SocketException e) {
                        log.error("Connection erro: {}", e.getMessage());
                        @NotNull Optional<@NotNull ClientConnection> optional = system.getConnection((SocketChannel) key.channel());
                        if (optional.isPresent()) try {optional.get().close();} catch (IOException ignore) {}
                    } catch (IOException ignore) {

                    }
                }

                keyIterator.remove();
            }
        }
    }

}
