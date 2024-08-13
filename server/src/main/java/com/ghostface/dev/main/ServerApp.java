package com.ghostface.dev.main;

import com.ghostface.dev.connection.JChatServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import java.net.InetSocketAddress;


public class ServerApp {
    public static void main(String[] args) throws IOException {
        @NotNull InetSocketAddress address = new InetSocketAddress("0.0.0.0",8080);
        JChatServer chatServer = new JChatServer(address);

        try {
            chatServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
