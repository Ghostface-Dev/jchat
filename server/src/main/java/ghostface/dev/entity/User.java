package ghostface.dev.entity;

import ghostface.dev.Client;
import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import ghostface.dev.account.Username;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.channels.SocketChannel;
import java.util.Objects;

public final class User {

    private final @NotNull Username username;
    private final @NotNull Email email;
    private final @NotNull Password password;
    private @Nullable Client connection;

    public User(@NotNull Username username, @NotNull Email email, @NotNull Password password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    // Getters

    public @NotNull Username getUsername() {
        return username;
    }

    public @NotNull Email getEmail() {
        return email;
    }

    public @NotNull Password getPassword() {
        return password;
    }

    public @Nullable Client getConnection() {
        return connection;
    }

    public void setChannel(@NotNull Client connection) throws IllegalArgumentException {
        if (!connection.getChannel().isConnected()) {
            throw new IllegalArgumentException("connection is not active");
        }

        this.connection = connection;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull User user = (User) object;
        return Objects.equals(username, user.username) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email);
    }
}
