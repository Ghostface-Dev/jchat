package ghostface.dev.packet;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public abstract class Packet {

    protected final @NotNull JsonObject data;
    protected final @NotNull Type type;

    protected Packet( @NotNull JsonObject data, @NotNull Type type) {
        data.keySet().clear();
        this.data = data;
        this.type = type;
    }

    // Getters

    public final @NotNull Type getType() {
        return type;
    }

    public enum Type {
        AUTHENTICATION,
        MESSAGE,
        REGISTRY,
        FAILED,
        SERVER_MESSAGE
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
