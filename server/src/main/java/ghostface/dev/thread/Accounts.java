package ghostface.dev.thread;

import ghostface.dev.entity.User;
import ghostface.dev.entity.SignIn;
import ghostface.dev.packet.ConnectionPacket;
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

    private final @NotNull Set<@NotNull User> users = ConcurrentHashMap.newKeySet();
    private final @NotNull BlockingQueue<@NotNull Runnable> queue = new LinkedBlockingQueue<>();
    // TODO register runnable
    public @NotNull CompletableFuture<@NotNull ConnectionPacket> register(@NotNull User user) {
        @NotNull CompletableFuture<@NotNull ConnectionPacket> future = new CompletableFuture<>();
        getInstance().queue.add(new RegisterRunnable(getInstance(), future, user));
        return future;
    }
    // TODO authentication runnable
    public @NotNull CompletableFuture<@NotNull ConnectionPacket> authenticate(@NotNull SignIn signInAccount) {
        @NotNull CompletableFuture<@NotNull ConnectionPacket> future = new CompletableFuture<>();
        getInstance().queue.add(new AuthenticationRunnable(getInstance(), future, signInAccount));
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
            } catch (@NotNull Throwable throwable) {
                // todo do something
            }
        }
    }

    // Getters

    @NotNull Set<@NotNull User> getAccounts() {
        return users;
    }

    public Accounts() {
        super("Accounts thread");
        setDaemon(false);

        start();
    }

}
