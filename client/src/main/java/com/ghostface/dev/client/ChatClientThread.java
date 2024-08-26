package com.ghostface.dev.client;


import com.ghostface.dev.utility.Mensseger;
import com.ghostface.dev.utility.Username;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.*;
import java.util.Iterator;

final class ChatClientThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ChatClientThread.class);

    private final @NotNull ChatClient client;
    private final @NotNull SocketChannel channel;
    private final @NotNull Selector selector;
    private boolean authenticated = false;

    public ChatClientThread(@NotNull ChatClient client) {
        this.client = client;

        @Nullable SocketChannel channel = client.getChannel();
        @Nullable Selector selector = client.getSelector();

        if (channel == null || !channel.isOpen()) {
            throw new IllegalArgumentException("Socket is not active");
        } else if (selector == null) {
            throw new IllegalArgumentException("Selector is null");
        }

        this.channel = channel;
        this.selector = selector;
    }

    @Override
    public void run() {
        while (channel.isOpen()) {
            @NotNull Iterator<@NotNull SelectionKey> keyIterator;
            @NotNull BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

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

                    if (key.isConnectable()) {
                        if (channel.finishConnect()) {
                            channel.register(selector, SelectionKey.OP_READ);
                            log.info("Connection {} is succesful", channel.getLocalAddress());
                            System.out.print("Enter the username: ");
                            @NotNull String username = in.readLine();

                            while (!Username.validate(username)) {
                                System.out.print("Enter the username: ");
                                username = in.readLine();
                            }

                            Mensseger.write(username, channel);
                        }
                    }

                    if (key.isReadable()) {

                        if (!authenticated) {

                            @Nullable String responseAuthentication = Mensseger.read(channel);

                            System.out.println(responseAuthentication);

                            if (!Boolean.parseBoolean(responseAuthentication)) {
                                System.err.println("Username Already exist");
                                channel.close();
                            } else {
                                authenticated = true;
                            }
                        }

                        // message
                        if (channel.isOpen()) {
                            @Nullable String response = Mensseger.read(channel);

                            if (response != null)
                                System.out.println(response);

                            new Thread(() -> {
                                try {
                                    @NotNull String msg = in.readLine();
                                    Mensseger.write(msg, channel);
                                } catch (IOException e) {
                                    log.error(e.getMessage());
                                    try {
                                        channel.close();
                                    } catch (IOException ignore) {}
                                };
                            }).start();
                        }

                    }

                } catch (IOException e) {
                    log.error("ERRO DO CARALHO -> {}", e.getMessage());
                    try {
                        channel.close();
                    } catch (IOException ignore) {}
                    break;
                }

            }

        }
    }

    public @NotNull ChatClient getClient() {
        return client;
    }
}
