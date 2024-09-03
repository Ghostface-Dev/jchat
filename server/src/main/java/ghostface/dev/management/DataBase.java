package ghostface.dev.management;

import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import ghostface.dev.connection.Account;
import ghostface.dev.connection.Client;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;

public final class DataBase extends Thread {

    private static final @NotNull DataBase instance = new DataBase();

    public static @NotNull DataBase getInstance() {
        return instance;
    }

    // Object

    private final @NotNull Set<@NotNull Account> accounts = ConcurrentHashMap.newKeySet();
    private final @NotNull Set<@NotNull Client> clients = ConcurrentHashMap.newKeySet();

    private final @NotNull BlockingQueue<@NotNull Runnable> queue = new LinkedBlockingQueue<>();

    public @NotNull CompletableFuture<@NotNull Account> authenticate(@NotNull Email email, @NotNull Password password) {
        @NotNull CompletableFuture<@NotNull Account> future = new CompletableFuture<>();
        getInstance().queue.add(new AuthenticationRunnable(getInstance(), email, password, future));

        return future;
    }

    public @NotNull CompletableFuture<@NotNull Boolean> register(@NotNull Account account) {
        @NotNull CompletableFuture<@NotNull Boolean> future = new CompletableFuture<>();
        getInstance().queue.add(new RegisterRunnable(getInstance(), account, future));

        return future;
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

    // Getters

    public @NotNull Optional<@NotNull Account> getAccount(@NotNull Client connection) {
        return accounts.stream().filter(account -> account.getClient() != null && account.getClient().equals(connection)).findFirst();
    }

    public @NotNull Optional<@NotNull Client> getClient(@NotNull Account account) {
        return accounts.stream().filter(acc -> acc.getClient() != null && acc.equals(account)).map(Account::getClient).findFirst();
    }

    public @NotNull Optional<@NotNull Client> getClient(@NotNull SelectionKey key) {
        @NotNull SocketChannel channel = (SocketChannel) key.channel();
        return clients.stream().filter(client -> client.getChannel().equals(channel)).findFirst();
    }

    public @NotNull Set<@NotNull Account> getAccounts() {
        return accounts;
    }

    public @NotNull Set<@NotNull Client> getClients() {
        return clients;
    }

    private DataBase() {
        super("Accounts thread");
        setDaemon(false);

        start();
    }
}
