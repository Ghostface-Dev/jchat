package ghostface.dev.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ghostface.dev.Client;
import ghostface.dev.util.PacketValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Optional;

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

                                // TODO add accounts thread
                                if (PacketValidator.isAuthentication(object)) {

                                } else if (PacketValidator.isRegistry(object)) {

                                } else if (PacketValidator.isMessage(object)) {
                                    // todo add message and user class
                                } else {
                                    throw new SocketException();
                                }

                            }

                        }

                    }

                // todo send packets
                } catch (SocketException e) {
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
