package ghostface.dev.packet;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public final class ErrorPacket extends Packet {

    public ErrorPacket(@NotNull String string) {
        super(new JsonObject(), Type.ERROR);
        data.addProperty("type", getType().name().toLowerCase());
        data.addProperty("error", string.toLowerCase());
    }
}
