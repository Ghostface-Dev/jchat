package com.ghostface.dev.main;

import com.ghostface.dev.JChatServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import java.net.InetSocketAddress;


public class ServerApp {
    public static void main(String[] args) throws IOException {
        @NotNull InetSocketAddress address = new InetSocketAddress("0.0.0.0",5551);

        @NotNull JChatServer server = new JChatServer(address);

        server.start();

    }
}
