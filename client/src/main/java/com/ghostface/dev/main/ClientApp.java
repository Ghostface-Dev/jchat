package com.ghostface.dev.main;


import com.ghostface.dev.ChatRoom;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ClientApp {
    public static void main(String[] args) {

        @NotNull InetSocketAddress address = new InetSocketAddress("localhost", 5551);

        @NotNull ChatRoom chat = new ChatRoom(address);

        try {
            chat.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
