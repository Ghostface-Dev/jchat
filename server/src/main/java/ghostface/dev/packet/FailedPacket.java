package ghostface.dev.packet;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public final class FailedPacket extends Packet {

    private final @NotNull Response response;

    public FailedPacket(@NotNull Response response) {
        super(new JsonObject(), Type.FAILED);
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
        DISCONNECT,
    }

}
