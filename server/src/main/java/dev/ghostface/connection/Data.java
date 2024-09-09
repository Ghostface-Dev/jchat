package dev.ghostface.connection;

import codes.ghostface.models.Email;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public final class Data {

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

}
