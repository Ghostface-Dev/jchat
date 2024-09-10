package dev.ghostface.server;

import codes.ghostface.ClientPacket;
import codes.ghostface.impl.client.AuthenticationPacket;
import codes.ghostface.impl.client.MessagePacket;
import codes.ghostface.impl.client.RegisterPacket;
import codes.ghostface.impl.server.ServerErrorPacket;
import codes.ghostface.impl.server.ServerMessagePacket;
import codes.ghostface.models.Message;
import codes.ghostface.utils.PacketUtils.*;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import dev.ghostface.connection.Account;
import dev.ghostface.connection.Client;
import dev.ghostface.connection.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.channels.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.Optional;

final class JChatServerThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(JChatServerThread.class);

    private final @NotNull JChatServer server;
    private final @NotNull ServerSocket socket;
    private final @NotNull Selector selector;

    public JChatServerThread(@NotNull JChatServer server) {
        this.server = server;
        @Nullable ServerSocket socket = server.getChannel();
        @Nullable Selector selector = server.getSelector();

        if (socket == null || !socket.isBound() || selector == null) {
            throw new IllegalArgumentException("Server is not active");
        }

        this.socket = socket;
        this.selector = selector;
    }

    @Override
    public void run() {
        log.info("Server is running on {} port", socket.getLocalPort());

        while (socket.isBound() && selector.isOpen()) {
            @NotNull Iterator<@NotNull SelectionKey> keyIterator;

            try {
                int readyChannels = selector.select();
                if (readyChannels == 0) continue;
                keyIterator = selector.selectedKeys().iterator();
            } catch (IOException e) {
                continue;
            } catch (ClosedSelectorException e) {
                break;
            }

            while (keyIterator.hasNext()) {
                @NotNull SelectionKey key = keyIterator.next();
                keyIterator.remove();

                if (key.isAcceptable()) {
                    @Nullable SocketChannel channel = null;
                    try {
                        channel = socket.accept().getChannel();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                        @NotNull Client client = new Client(channel.socket(), server);
                        server.getClients().add(client);
                        log.info("Success authentication for {}", client.getSocket().getLocalSocketAddress());
                    } catch (@NotNull Throwable throwable) {
                        if (channel != null) {
                            try {channel.close();
                            } catch (IOException ignore) {}
                        }
                    }
                }

                if (key.isReadable()) {
                    @NotNull SocketChannel channel = (SocketChannel) key.channel();
                    @NotNull Optional<@NotNull Client> optional = server.getClient(key);

                    if (!optional.isPresent()) {
                        try {channel.close();}
                        catch (IOException ignore) {}
                    } else {
                        @NotNull Client client = optional.get();

                        try {
                            @Nullable ClientPacket packet = client.read();
                            if (packet == null) continue;

                            @NotNull ClientCheckers checkers = packet.getUtils().getClientHandler(packet).getCheckers();
                            if (checkers.isAuthentication()) {
                                @NotNull AuthenticationPacket auth = (AuthenticationPacket) packet;
                                Data.getInstance().authenticate(auth.getEmail(), auth.getPassword(), client);
                            } else if (checkers.isRegister()) {
                                @NotNull RegisterPacket register = (RegisterPacket) packet;
                                // TODO register
                            } else if (checkers.isMessage()) {
                                if (!client.isAuthenticated()) {
                                    throw new SocketException("User is not authenticated");
                                }
                                @NotNull MessagePacket msg = (MessagePacket) packet;
                                @NotNull Optional<@NotNull Account> accountOptional = Data.getInstance().getAccount(client);
                                if (!accountOptional.isPresent()) {
                                    throw new SocketException("User is not authenticated");
                                }
                                // TODO add events
                                @NotNull Message message = new Message(accountOptional.get().getUsername(), msg.getText(), Instant.now());
                                @NotNull ServerMessagePacket messagePacket = new ServerMessagePacket(message);
                                client.write(messagePacket);
                            } else {
                                throw new SocketException("invalid Operation");
                            }

                        } catch (@NotNull Throwable throwable) {
                            @NotNull ServerErrorPacket packet = new ServerErrorPacket(throwable, OffsetDateTime.now());
                            try {client.write(packet);
                            } catch (IOException ignore) {}
                            client.close();
                        }
                    }
                }
            }
        }
    }
}
