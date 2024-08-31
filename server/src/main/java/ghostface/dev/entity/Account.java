package ghostface.dev.entity;

import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import org.jetbrains.annotations.NotNull;

public final class Account {

    // todo add user
    private final @NotNull Email email;
    private final @NotNull Password password;

    public Account(@NotNull Email email, @NotNull Password password) {
        this.email = email;
        this.password = password;
    }

    public @NotNull Email getEmail() {
        return email;
    }

    public @NotNull Password getPassword() {
        return password;
    }

}
