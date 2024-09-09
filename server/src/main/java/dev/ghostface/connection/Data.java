package dev.ghostface.connection;

import codes.ghostface.models.Email;
import codes.ghostface.models.Username;
import dev.ghostface.exception.AccountException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.AuthenticationException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

public final class Data extends Thread {

    // Static Initializers

    private static final @NotNull Data intance;

    static {
        intance = new Data();
    }

    public static @NotNull Data getInstance() {
        return intance;
    }

    // Objects

    private final @NotNull Set<@NotNull Account> accounts = ConcurrentHashMap.newKeySet();
    private final @NotNull BlockingQueue<@NotNull Runnable> queue = new LinkedBlockingQueue<>();

    public @NotNull CompletableFuture<@NotNull Boolean> register(@NotNull Account account) {
        @NotNull CompletableFuture<@NotNull Boolean> future = new CompletableFuture<>();
        intance.queue.add(new RegisterRunnable(account,future));
        return future;
    }

    public @NotNull CompletableFuture<@NotNull Account> authenticate(@NotNull Email email, @NotNull String password, @NotNull Client client) {
        @NotNull CompletableFuture<@NotNull Account> future = new CompletableFuture<>();
        intance.queue.add(new AuthenticationRunnable(email, password, client, future));
        return future;
    }

    // Getters

    public @NotNull Optional<@NotNull Account> getAccount(@NotNull Email email, @NotNull String password) {
        return accounts.stream().filter(account -> account.getEmail().equals(email) && account.getPassword().equals(password)).findFirst();
    }

    public @NotNull Optional<@NotNull Account> getAccount(@NotNull Client client) {
        return accounts.stream().filter(account -> account.getClient() != null && account.getClient().equals(client)).findFirst();
    }

    public @NotNull Set<@NotNull Account> getAccounts() {
        return accounts;
    }

    @Override
    public void run() {
        while (isAlive()) {
            try {
                @Nullable Runnable runnable = queue.poll(1, TimeUnit.SECONDS);
                if (runnable == null) continue;
                runnable.run();
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private Data() {
        super("Data thread");
        setDaemon(false);

        start();
    }

    // Classes

    private final class AuthenticationRunnable implements Runnable {

        private final @NotNull Email email;
        private final @NotNull String password;
        private final @NotNull Client client;
        private final @NotNull CompletableFuture<@NotNull Account> future;

        public AuthenticationRunnable(@NotNull Email email, @NotNull String password, @NotNull Client client, @NotNull CompletableFuture<@NotNull Account> future) {
            this.email = email;
            this.password = password;
            this.client = client;
            this.future = future;
        }

        @Override
        public void run() {
            @NotNull Optional<@NotNull Account> account = getAccount(email, password);

            if (!account.isPresent()) {
                future.completeExceptionally(new AccountException("Account not found or password incorrect"));
            } else if (account.get().isOnline()) {
                future.completeExceptionally(new AccountException("Account already in use"));
            } else if (client.isAuthenticated()) {
                future.completeExceptionally(new AuthenticationException("Client already authenticated"));
            } else {
                account.get().setClient(client);
            }
        }
    }

    private final class RegisterRunnable implements Runnable {

        private final @NotNull Email email;
        private final @NotNull Username username;
        private final @NotNull String password;

        private final @NotNull CompletableFuture<@NotNull Boolean> future;

        public RegisterRunnable(@NotNull Account account, @NotNull CompletableFuture<@NotNull Boolean> future) {
            this.email = account.getEmail();
            this.username = account.getUsername();
            this.password = account.getPassword();
            this.future = future;
        }


        @Override
        public void run() {
            boolean existEmail = accounts.stream().anyMatch(account -> account.getEmail().equals(this.email));
            boolean existUsername = accounts.stream().anyMatch(account -> account.getUsername().equals(this.username));

            if (existEmail) {
                future.completeExceptionally(new AccountException("Email already in use"));
            } else if (existUsername) {
                future.completeExceptionally(new AccountException("Username already in use"));
            } else {
                @NotNull Account account = new Account(email, username, password);
                accounts.add(account);
                future.complete(true);
            }
        }
    }
}
