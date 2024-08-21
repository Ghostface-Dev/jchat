package com.ghostface.dev.client;

import com.ghostface.dev.utility.Mensseger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
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

        @Nullable Socket socket = client.getSocket();
        @Nullable Selector selector = client.getSelector();

        if (socket == null) {
            throw new IllegalArgumentException("Socket is null");
        } else if (selector == null) {
            throw new IllegalArgumentException("Selector is null");
        }

        this.channel = socket.getChannel();
        this.selector = selector;
    }

    @Override
    public void run() {
        log.info("connected to {}", channel.socket().getInetAddress().getHostAddress());

        while (selector.isOpen()) {

           try {
               selector.select();
               @NotNull Iterator<@NotNull SelectionKey> keyIterator = selector.selectedKeys().iterator();

               while (keyIterator.hasNext()) {
                   @NotNull SelectionKey key = keyIterator.next();
                   keyIterator.remove();

                   if (key.isConnectable()) {
                       if (!channel.finishConnect()) {
                           channel.close();
                       } else {
                           channel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                       }
                   }

                   if (key.isWritable()) {
                       Mensseger.write(channel, "Hello world");
                   }

               }

           } catch (IOException e) {
               System.err.println(e.getMessage());
           }
       }
    }

    public @NotNull ChatClient getClient() {
        return client;
    }
}
