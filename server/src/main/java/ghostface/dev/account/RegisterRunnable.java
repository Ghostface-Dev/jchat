package ghostface.dev.account;

import ghostface.dev.entity.Account;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

final class RegisterRunnable implements Runnable {

    private final @NotNull Accounts accounts;
    private final @NotNull CompletableFuture<@NotNull Boolean> future;
    private final @NotNull Account account;

    public RegisterRunnable(@NotNull Accounts accounts, @NotNull CompletableFuture<@NotNull Boolean> future, @NotNull Account account) {
        this.accounts = accounts;
        this.future = future;
        this.account = account;
    }

    @Override
    public void run() {

    }

}
