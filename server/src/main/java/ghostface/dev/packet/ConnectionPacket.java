package ghostface.dev.packet;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public final class ConnectionPacket extends Packet {
    public ConnectionPacket(@NotNull Scope scope) {
        super(new JsonObject(), Type.CONNECTION);
        data.addProperty("scope", scope.name().toLowerCase());
    }

    public enum Scope {
        SUCCESS,
        DISCONNECT
    }
}
