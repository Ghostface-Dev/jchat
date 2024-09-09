package dev.ghostface.connection;

import codes.ghostface.models.Email;
import dev.ghostface.exception.AccountException;

import dev.ghostface.server.JChatServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Optional;

public final class Client {

    private final @NotNull SocketChannel channel;
    private final @NotNull JChatServer server;

    public Client(@NotNull SocketChannel channel, @NotNull JChatServer server) {
        this.channel = channel;
        this.server = server;
    }

    public void close() {
        getServer().getClients().remove(this);
        try {
            deauthenticate();
            getChannel().close();
        } catch (AuthenticationException | IOException ignore) {}
    }

    public void authenticate(@NotNull Email email, @NotNull String password) throws AuthenticationException {
        if (this.isAuthenticated()) {
            throw new AuthenticationException("Client already authenticated");
        }

        @NotNull Optional<@NotNull Account> optional = Data.getInstance().getAccount(email, password);

        if (!optional.isPresent()) {
            throw new AccountException("Account not found");
        }
        // Checks if account is online
        if (optional.get().isOnline()) {
            throw new AccountException("Account already in use");
        }
        // finish
        optional.get().setClient(this);
    }

    public void deauthenticate() throws AuthenticationException {
        @NotNull Optional<@NotNull Account> optionalAccount = Data.getInstance().getAccount(this);

        if (!isAuthenticated() || !optionalAccount.isPresent()) {
            throw new AuthenticationException("Client already deauthenticated");
        }

        optionalAccount.get().setClient(null);
    }

    public boolean isAuthenticated() {
        return Data.getInstance().getAccount(this).isPresent();
    }

    // Getters

    public @NotNull SocketChannel getChannel() {
        return channel;
    }

    public @NotNull JChatServer getServer() {
        return server;
    }

    // Native

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Client client = (Client) object;
        return Objects.equals(channel, client.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(channel);
    }
}
