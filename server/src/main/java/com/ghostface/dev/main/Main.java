package com.ghostface.dev.main;

import com.ghostface.dev.JChat;
import com.jlogm.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;


public class Main {

    private static final @NotNull Logger log = Logger.create(Main.class);

    public static void main(String[] args) {
        @NotNull InetSocketAddress address = new InetSocketAddress("0.0.0.0",5551);
        JChat chat = new JChat(address);

        try {

            if (!chat.start()) {
                log.info("Cannot start Server cause another is running");
            } else {
                log.info().log("server is running on " + chat.getAddress().getPort() + " port");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
