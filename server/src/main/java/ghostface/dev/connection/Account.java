package ghostface.dev.connection;

import ghostface.dev.User;
import ghostface.dev.account.Email;
import ghostface.dev.account.Password;

import ghostface.dev.exception.AccountException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public final class Account {

    private final @NotNull Email email;
    private final @NotNull Password password;
    private final @NotNull User user;
    private @Nullable Client client;

    public Account(@NotNull Email email, @NotNull Password password, @NotNull User user) {
        this.email = email;
        this.password = password;
        this.user = user;
    }

    void online(@NotNull Client client) throws AccountException {
        if (this.client != null) {
            throw new AccountException("Account already in use");
        }
        this.client = client;
    }

    void offline() {
        if (this.client == null) {
            return;
        }
        this.client = null;
    }

    public @NotNull Email getEmail() {
        return email;
    }

    public @NotNull Password getPassword() {
        return password;
    }

    public @NotNull User getUser() {
        return user;
    }

    public @Nullable Client getClient() {
        return client;
    }

    // Natives

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Account account = (Account) object;
        return Objects.equals(email, account.email) && Objects.equals(user, account.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, user);
    }
}
