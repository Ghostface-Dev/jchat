package ghostface.dev;

import ghostface.dev.account.Username;
import ghostface.dev.connection.Client;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.Objects;

public final class User {

    private final @NotNull Username username;
    private final @NotNull OffsetDateTime time;

    public User(@NotNull Username username, OffsetDateTime time) {
        this.username = username;
        this.time = time;
    }

    // Getters

    public @NotNull Username getUsername() {
        return username;
    }

    public @NotNull OffsetDateTime getTime() {
        return time;
    }

    public void setChannel(@NotNull Client connection) throws IllegalArgumentException {
        if (!connection.getChannel().isConnected()) {
            throw new IllegalArgumentException("connection is not active");
        }
    }

    // Natives

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull User user = (User) object;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

}
