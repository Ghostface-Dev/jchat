package ghostface.dev.account;

import ghostface.dev.entity.SignIn;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

final class AuthenticationRunnable implements Runnable {

    private final @NotNull Accounts accounts;
    private final @NotNull CompletableFuture<@NotNull Boolean> future;
    private final @NotNull SignIn account;

    public AuthenticationRunnable(@NotNull Accounts accounts, @NotNull CompletableFuture<@NotNull Boolean> future, @NotNull SignIn account) {
        this.accounts = accounts;
        this.future = future;
        this.account = account;
    }

    @Override
    public void run() {

    }

}
