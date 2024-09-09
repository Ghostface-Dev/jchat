package dev.ghostface.connection;

import codes.ghostface.models.Email;
import codes.ghostface.models.Username;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Account {

    private final @NotNull Email email;
    private final @NotNull Username username;
    private final @NotNull String password;

    private @Nullable Client client;

    public Account(@NotNull Email email, @NotNull Username username, @NotNull String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public boolean isOnline() {
        return client != null;
    }

    // Getters

    public @NotNull Email getEmail() {
        return email;
    }

    public @NotNull Username getUsername() {
        return username;
    }

    public @Nullable Client getClient() {
        return client;
    }

    @NotNull String getPassword() {
        return password;
    }

    void setClient(@Nullable Client client) {
        this.client = client;
    }
}
