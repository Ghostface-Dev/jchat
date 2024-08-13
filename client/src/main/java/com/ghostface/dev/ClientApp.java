package com.ghostface.dev;

import com.ghostface.dev.connection.JChatClient;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

public class ClientApp {
    public static void main(String[] args) {
        @NotNull InetSocketAddress address = new InetSocketAddress("localhost", 5551);
        @NotNull JChatClient client = new JChatClient(address);
        try {
            client.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
