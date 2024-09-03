package ghostface.dev.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ghostface.dev.Message;
import ghostface.dev.User;
import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import ghostface.dev.account.Username;
import ghostface.dev.connection.Account;
import ghostface.dev.connection.Client;
import ghostface.dev.exception.AccountException;
import ghostface.dev.exception.AuthenticationException;
import ghostface.dev.management.DataBase;
import ghostface.dev.packet.ConnectionPacket;
import ghostface.dev.packet.ErrorPacket;
import ghostface.dev.packet.Packet;
import ghostface.dev.util.PacketValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.OffsetDateTime;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

final class ServerThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ServerThread.class);

    private final @NotNull ServerJChat chat;
    private final @NotNull ServerSocketChannel channel;
    private final @NotNull Selector selector;

    public ServerThread(@NotNull ServerJChat chat) {
        this.chat = chat;

        @Nullable ServerSocketChannel channel = chat.getChannel();
        @Nullable Selector selector = chat.getSelector();

        if ((channel == null || !channel.socket().isBound()) || selector == null) {
            throw new IllegalArgumentException("Server is not active");
        }

        this.channel = channel;
        this.selector = selector;
    }

    @Override
    public void run() {
        while (channel.socket().isBound() && selector.isOpen()) {
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
                keyIterator.remove();

                try {
                    if (key.isAcceptable()) {
                        try {
                            @NotNull SocketChannel socket = this.channel.accept();
                            socket.configureBlocking(false);
                            socket.register(selector, SelectionKey.OP_READ);
                            @NotNull Client client = new Client(chat.getDataBase(), socket);
                            chat.getDataBase().getClients().add(client);
                        } catch (@NotNull Throwable throwable) {
                            throw new SocketException(throwable.getMessage());
                        }
                    }

                    if (key.isReadable()) {
                        @NotNull Optional<@NotNull Client> clientOptional = chat.getDataBase().getClient(key);

                        if (clientOptional.isPresent()) {
                            @Nullable String reponse = clientOptional.get().read(key);
                            if (reponse != null) {
                                @NotNull JsonObject object = JsonParser.parseString(reponse).getAsJsonObject();

                                if (PacketValidator.isAuthentication(object)) {
                                    @NotNull Email email = Email.parse(object.get("email").getAsString());
                                    @NotNull Password password = Password.parse(object.get("password").getAsString());
                                    try {
                                        clientOptional.get().authenticate(email, password);
                                    } catch (AuthenticationException | AccountException e) {
                                        @NotNull Packet packet = new ErrorPacket(e.getMessage());
                                        clientOptional.get().write(packet.toString());
                                    }
                                }

                                if (PacketValidator.isRegistry(object)) {
                                    @NotNull Email email = Email.parse(object.get("email").getAsString());
                                    @NotNull Password password = Password.parse(object.get("password").getAsString());
                                    @NotNull Username username = Username.parse(object.get("username").getAsString());

                                    @NotNull Account account = new Account(email, password, new User(username, OffsetDateTime.now()));
                                    @NotNull CompletableFuture<@NotNull Boolean> future = chat.getDataBase().register(account);
                                    try {
                                        if (future.get()) {
                                            @NotNull Packet packet = new ConnectionPacket(ConnectionPacket.Scope.SUCCESS);
                                        }
                                    } catch (ExecutionException e) {
                                        @NotNull Packet packet = new ErrorPacket(e.getMessage());
                                        clientOptional.get().write(packet.toString());
                                        throw new SocketException(e.getMessage());
                                    } catch (InterruptedException e) {
                                        throw new SocketException(e.getMessage());
                                    }
                                }

                                if (PacketValidator.isMessage(object)) {
                                    if (!clientOptional.get().isAuthenticated()) {
                                        @NotNull Packet packet = new ErrorPacket("Client is not authenticated");
                                        clientOptional.get().write(packet.toString());
                                        throw new SocketException("Client not authenticated");
                                    } else {
                                        @NotNull Optional<@NotNull Account> account = chat.getDataBase().getAccount(clientOptional.get());
                                        if (account.isEmpty()) {
                                            @NotNull Packet packet = new ErrorPacket("Cannot find account");
                                            clientOptional.get().write(packet.toString());
                                            throw new SocketException("Client account not found");
                                        }
                                        @NotNull String text = object.get("text").getAsString();
                                        @NotNull Username username = account.get().getUser().getUsername();
                                        @NotNull OffsetDateTime time = OffsetDateTime.parse(object.get("time").getAsString());

                                        @NotNull Message message = new Message(text, username, time);
                                        clientOptional.get().broadcast(message.toString());
                                    }
                                }
                            }
                        }

                    }

                } catch (IllegalArgumentException | SocketException e) {
                    log.error(e.getMessage());
                    @NotNull SocketChannel channel = (SocketChannel) key.channel();
                    try {channel.close();} catch (IOException ignore) {}
                } catch (IOException e) {
                    log.error("I/O Error: {}", e.getMessage());
                }
            }
        }
    }
}
