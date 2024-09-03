package ghostface.dev.management;

import ghostface.dev.User;
import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import ghostface.dev.account.Username;
import ghostface.dev.connection.Account;
import ghostface.dev.exception.AuthenticationException;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

final class RegisterRunnable implements Runnable {

    private final @NotNull DataBase dataBase;
    private final @NotNull Email email;
    private final @NotNull Password password;
    private final @NotNull Username username;
    private final @NotNull CompletableFuture<@NotNull Boolean> future;

    public RegisterRunnable(@NotNull DataBase dataBase, @NotNull Account account, @NotNull CompletableFuture<@NotNull Boolean> future) {
        this.dataBase = dataBase;
        this.email = account.getEmail();
        this.password = account.getPassword();
        this.username = account.getUser().getUsername();
        this.future = future;
    }

    @Override
    public void run() {

        boolean existsEmail = dataBase.getAccounts().stream().anyMatch(acc -> acc.getEmail().equals(email));
        boolean existsUsername = dataBase.getAccounts().stream().anyMatch(acc -> acc.getUser().getUsername().equals(username));

        try {
            if (existsEmail) {
                throw new AuthenticationException("Email already exist");
            } else if (existsUsername) {
                throw new AuthenticationException("Username already exist");
            } else {
                @NotNull Account account = new Account(email, password, new User(username, OffsetDateTime.now()));
                dataBase.getAccounts().add(account);
                future.complete(true);
            }
        } catch (AuthenticationException e) {
            future.completeExceptionally(e);
        }

    }
}
