package ghostface.dev.thread;

import ghostface.dev.account.Email;
import ghostface.dev.account.Username;
import ghostface.dev.entity.User;
import ghostface.dev.packet.ConnectionPacket;
import ghostface.dev.packet.ConnectionPacket.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

final class RegisterRunnable implements Runnable {

    private final @NotNull Accounts accounts;
    private final @NotNull CompletableFuture<@NotNull ConnectionPacket> future;
    private final @NotNull User user;

    public RegisterRunnable(@NotNull Accounts accounts, @NotNull CompletableFuture<@NotNull ConnectionPacket> future, @NotNull User user) {
        this.accounts = accounts;
        this.future = future;
        this.user = user;
    }

    @Override
    public void run() {
        @NotNull Username username = user.getUsername();
        @NotNull Email email = user.getEmail();

        boolean existsEmail = accounts.getAccounts().stream().anyMatch(acc -> acc.getEmail().equals(email));
        boolean existsUsername = accounts.getAccounts().stream().anyMatch(acc -> acc.getUsername().equals(username));

        if (existsEmail) {
            future.complete(new ConnectionPacket(Response.EXISTING_EMAIL));
        } else if (existsUsername) {
            future.complete(new ConnectionPacket(Response.EXISTING_USERNAME));
        } else {
            accounts.getAccounts().add(user);
            future.complete(new ConnectionPacket(Response.SUCCESS));
        }

    }

}
