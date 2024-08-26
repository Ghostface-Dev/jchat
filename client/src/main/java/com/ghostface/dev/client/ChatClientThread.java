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
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

final class ChatClientThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(ChatClientThread.class);

    private final @NotNull ChatClient client;
    private final @NotNull SocketChannel channel;
    private final @NotNull Selector selector;

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

        while (selector.isOpen()) {

            try {
                if (selector.select() > 0) {
                    @NotNull Iterator<@NotNull SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    @NotNull BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

                    while (keyIterator.hasNext()) {
                        @NotNull SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (!key.isValid())
                            continue;

                        if (key.isConnectable()) {
                            if (channel.finishConnect()) {
                                channel.register(selector, SelectionKey.OP_READ);
                                log.info("Connection {} is Succesfull", channel.getLocalAddress());

                                System.out.print("Enter the username: ");
                                @NotNull String username = in.readLine();

                                while (!Username.validate(username)) {
                                    System.out.print("Enter the username: ");
                                    username = in.readLine();
                                }

                                Mensseger.write(username, channel);

                            } else {
                                key.cancel();
                            }
                        }

                        if (key.isReadable()) {

                            System.out.println("Checking...");
                            Thread.sleep(1000);

                            if (!Boolean.parseBoolean(Mensseger.read(channel))) {
                                System.err.println("Username Already exist");
                                channel.close();
                            } else {
                                System.out.println("Welcome ");
                            }

                                // msg
                            while (selector.isOpen()) {

                                new Thread(() -> {
                                    @NotNull String msg = null;
                                    try {
                                        msg = in.readLine();
                                        Mensseger.write(msg, channel);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }).start();

                                @Nullable String response = Mensseger.read(channel);

                                if (response != null)
                                    System.out.println(response);

                            }

                        }
                    }

                }
            } catch (InterruptedException ignore) {

            } catch (SocketException e) {
                System.err.println("Lost Connection");
                break;
            } catch (IOException e) {
                log.error(e.getMessage());
            }

        }

    }

    public @NotNull ChatClient getClient() {
        return client;
    }
}
