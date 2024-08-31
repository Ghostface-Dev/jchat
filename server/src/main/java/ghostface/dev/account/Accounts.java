package ghostface.dev.account;

import ghostface.dev.entity.Account;
import ghostface.dev.entity.SignIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.*;

public final class Accounts extends Thread {

    private static final @NotNull Accounts instance = new Accounts();

    public static @NotNull Accounts getInstance() {
        return instance;
    }

    // Object

    private final @NotNull Set<@NotNull Account> accounts = ConcurrentHashMap.newKeySet();
    private final @NotNull BlockingQueue<@NotNull Runnable> queue = new LinkedBlockingQueue<>();

    // TODO register runnable
    public @NotNull CompletableFuture<@NotNull Boolean> register(@NotNull Account account) {
        @NotNull CompletableFuture<@NotNull Boolean> future = new CompletableFuture<>();
        getInstance().queue.add(new RegisterRunnable(getInstance(), future, account));
        return future;
    }
    // TODO authentication runnable
    public @NotNull CompletableFuture<@NotNull Boolean> authenticate(@NotNull SignIn signInAccount) {
        @NotNull CompletableFuture<@NotNull Boolean> future = new CompletableFuture<>();
        getInstance().queue.add(new AuthenticationRunnable(getInstance(),future ,signInAccount));
        return future;
    }

    public Accounts() {
        super("Accounts thread");
        setDaemon(false);

        start();
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
            } catch (@NotNull Throwable throwable) {
                // todo do something
            }
        }
    }

}
