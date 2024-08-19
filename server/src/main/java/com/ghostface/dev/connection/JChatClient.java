package com.ghostface.dev.connection;

import com.ghostface.dev.JChat;
import com.ghostface.dev.entity.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Socket;
import java.util.Objects;

public final class JChatClient {

    private final @NotNull JChat chat;
    private final @NotNull Socket socket;
    private @Nullable User user;

    private boolean authenticated;

    public JChatClient(@NotNull JChat chat, @NotNull Socket socket) {
        this.chat = chat;
        this.socket = socket;
        this.authenticated = false;
        chat.getClients().add(this);
    }

    // getters

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public @NotNull Socket getSocket() {
        return socket;
    }

    @NotNull JChat getChat() {
        return chat;
    }

    public @Nullable User getUser() {
        return user;
    }

    public void setUser(@NotNull User user) {
        if (this.user != null) {
            throw new IllegalArgumentException("User is already defined");
        }
        this.user = user;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull JChatClient client = (JChatClient) object;
        return Objects.equals(socket, client.socket) && Objects.equals(user, client.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket, user);
    }
}
