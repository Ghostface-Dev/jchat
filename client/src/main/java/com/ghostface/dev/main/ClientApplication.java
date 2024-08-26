package com.ghostface.dev.main;

import com.ghostface.dev.client.ChatClient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class ClientApplication {

    private static final Logger log = LoggerFactory.getLogger(ClientApplication.class);

    public static void main(String[] args) {
        @NotNull ChatClient client = new ChatClient();
        try {
            if (!client.join(new InetSocketAddress("localhost", 5551))) {
                log.error("Cannot run client");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
