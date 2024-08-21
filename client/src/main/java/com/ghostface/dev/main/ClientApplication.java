package com.ghostface.dev.main;

import com.ghostface.dev.client.ChatClient;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class ClientApplication {

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        try {
            client.join(new InetSocketAddress("localhost", 5551));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
