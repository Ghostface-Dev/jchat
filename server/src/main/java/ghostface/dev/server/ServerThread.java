package ghostface.dev.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ghostface.dev.Client;
import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import ghostface.dev.account.Username;
import ghostface.dev.entity.Message;
import ghostface.dev.entity.SignIn;
import ghostface.dev.entity.User;
import ghostface.dev.packet.ConnectionPacket;
import ghostface.dev.packet.Packet;
import ghostface.dev.packet.ServerMessagePacket;
import ghostface.dev.util.PacketValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
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
                            @NotNull Client client = new Client(chat, socket);
                            chat.getClients().add(client);
                        } catch (@NotNull Throwable throwable) {
                            throw new SocketException();
                        }
                    }

                    if (key.isReadable()) {
                        @NotNull Optional<@NotNull Client> clientOptional = chat.getClient(key);

                        if (!clientOptional.isPresent()) {
                            throw new SocketException();
                        } else {
                            @Nullable String response = clientOptional.get().read(key);

                            if (response != null) {
                                @NotNull JsonObject object = JsonParser.parseString(response).getAsJsonObject();

                                if (PacketValidator.isAuthentication(object)) {
                                    @NotNull Email email = Email.parse(object.get("email").getAsString());
                                    @NotNull Password password = Password.parse(object.get("password").getAsString());
                                    @NotNull SignIn sign = new SignIn(email, password);

                                    @NotNull CompletableFuture<@NotNull ConnectionPacket> future = chat.getAccounts().authenticate(sign);
                                    @NotNull Packet packet = future.get();
                                    clientOptional.get().write(packet.toString());

                                } else if (PacketValidator.isRegistry(object)) {
                                    @NotNull Email email = Email.parse(object.get("email").getAsString());
                                    @NotNull Password password = Password.parse(object.get("password").getAsString());
                                    @NotNull Username username = Username.parse(object.get("username").getAsString());
                                    @NotNull User user = new User(username, email, password);

                                    @NotNull CompletableFuture<@NotNull ConnectionPacket> future = chat.getAccounts().register(user);
                                    @NotNull Packet packet = future.get();
                                    clientOptional.get().write(packet.toString());

                                } else if (PacketValidator.isMessage(object)) {
                                    @NotNull Optional<@NotNull User> userOptional = chat.getAccounts().getUser(clientOptional.get());
                                    if (!userOptional.isPresent()) {
                                        throw new SocketException();
                                    } else {
                                        userOptional.get().setChannel(clientOptional.get());
                                        @NotNull Username username = userOptional.get().getUsername();

                                        @NotNull Message message = new Message(object.get("content").getAsString(), username, OffsetDateTime.parse(object.get("time").getAsString()));
                                        @NotNull Packet packet = new ServerMessagePacket(message);
                                        clientOptional.get().write(packet.toString());
                                    }
                                } else {
                                    throw new SocketException();
                                }
                            }
                        }
                    }
                    // todo send packets
                } catch (IllegalArgumentException | SocketException | InterruptedException | ExecutionException e) {
                    log.error(e.getMessage());
                    @NotNull SocketChannel channel = (SocketChannel) key.channel();
                    try {channel.close();} catch (IOException ignore) {}
                } catch (ClosedSelectorException ignore) {

                } catch (IOException e) {
                    log.error("I/O Error: {}", e.getMessage());
                }

            }

        }
    }

}
