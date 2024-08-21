package com.ghostface.dev.main;

import com.ghostface.dev.server.ChatServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class ServerApplication {

    public static void main(String[] args) {
        try {
            @NotNull ChatServer server = new ChatServer();
            server.start(new InetSocketAddress("0.0.0.0", 5551));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
