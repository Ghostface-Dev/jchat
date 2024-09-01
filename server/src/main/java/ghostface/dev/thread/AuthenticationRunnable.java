package ghostface.dev.thread;

import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import ghostface.dev.entity.User;
import ghostface.dev.entity.SignIn;
import ghostface.dev.packet.ConnectionPacket;
import ghostface.dev.packet.ConnectionPacket.*;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

final class AuthenticationRunnable implements Runnable {

    private final @NotNull Accounts accounts;
    private final @NotNull CompletableFuture<@NotNull ConnectionPacket> future;
    private final @NotNull SignIn signIn;

    public AuthenticationRunnable(@NotNull Accounts accounts, @NotNull CompletableFuture<@NotNull ConnectionPacket> future, @NotNull SignIn signIn) {
        this.accounts = accounts;
        this.future = future;
        this.signIn = signIn;
    }

    @Override
    public void run() {
        @NotNull Email email = signIn.getEmail();
        @NotNull Password password = signIn.getPassword();

        @NotNull Optional<@NotNull User> account = accounts.getAccounts().stream().filter(acc -> acc.getEmail().equals(email)).findFirst();

        if (!account.isPresent()) {
            future.complete(new ConnectionPacket(Response.NOT_FOUND));
        } else if (!account.get().getPassword().equals(password)) {
            future.complete(new ConnectionPacket(Response.NOT_FOUND));
        } else {
           future.complete(new ConnectionPacket(Response.SUCCESS));
        }

    }

}
