package ghostface.dev.management;

import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import ghostface.dev.connection.Account;
import ghostface.dev.exception.AuthenticationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

final class AuthenticationRunnable implements Runnable {

    private final @NotNull DataBase dataBase;
    private final @NotNull Email email;
    private final @NotNull Password password;
    private final @NotNull CompletableFuture<@NotNull Account> future;

    public AuthenticationRunnable(@NotNull DataBase dataBase, @NotNull Email email, @NotNull Password password, @NotNull CompletableFuture<@NotNull Account> future) {
        this.dataBase = dataBase;
        this.email = email;
        this.password = password;
        this.future = future;
    }

    @Override
    public void run() {
        @Nullable Account account = dataBase.getAccounts().stream().filter(acc -> acc.getEmail().equals(email)).findFirst().orElse(null);

        try {
            if (account == null) {
                throw new AuthenticationException("Email not found");
            } else if (!account.getPassword().equals(password)) {
                throw new AuthenticationException("Email or password is incorrect");
            } else {
                future.complete(account);
            }
        } catch (AuthenticationException e) {
            future.completeExceptionally(e);
        }

    }
}
