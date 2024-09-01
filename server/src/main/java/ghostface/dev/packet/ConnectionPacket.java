package ghostface.dev.packet;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public final class ConnectionPacket extends Packet {

    private final @NotNull Response response;

    public ConnectionPacket(@NotNull Response response) {
        super(new JsonObject(), Type.CONNECTION);
        this.response = response;
        data.addProperty("type", getType().name().toLowerCase());
        data.addProperty("response", getResponse().name().toLowerCase());
    }

    public @NotNull Response getResponse() {
        return response;
    }

    public enum Response {
        NOT_FOUND,
        EXISTING_USERNAME,
        EXISTING_EMAIL,
        SUCCESS,
        DISCONNECT,
    }

}
