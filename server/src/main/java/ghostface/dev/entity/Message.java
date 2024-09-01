package ghostface.dev.entity;

import ghostface.dev.account.Username;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class Message {

    private final @NotNull String text;
    private final @NotNull Username username;
    private final @NotNull OffsetDateTime time;

    public Message(@NotNull String text, @NotNull Username username, @NotNull OffsetDateTime time) {
        this.text = text;
        this.username = username;
        this.time = time;
    }

    public @NotNull String getText() {
        return text;
    }

    public @NotNull Username getUsername() {
        return username;
    }

    public @NotNull OffsetDateTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        @NotNull String time = this.time.format(DateTimeFormatter.ofPattern("yy/MM/dd HH:mm"));
        return "[" + time + "] " + this.username + ": " + this.text;
    }
}
