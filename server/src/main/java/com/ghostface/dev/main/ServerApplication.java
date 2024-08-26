package com.ghostface.dev.main;

import com.ghostface.dev.server.ChatServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;

public final class ServerApplication {

    private static final Logger log = LoggerFactory.getLogger(ServerApplication.class);

    public static void main(String[] args) {

        try {
            @NotNull ChatServer server = new ChatServer();
            if (!server.start(new InetSocketAddress("0.0.0.0", 5551))) {
                throw new IllegalArgumentException("Cannot run server");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
