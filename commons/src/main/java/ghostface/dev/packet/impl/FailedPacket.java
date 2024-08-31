package ghostface.dev.packet.impl;

import com.google.gson.JsonObject;
import ghostface.dev.packet.Packet;
import org.jetbrains.annotations.NotNull;

public final class FailedPacket extends Packet {

    // static

    public static boolean validate(@NotNull JsonObject object) {
        if (!object.has("type") || !object.get("type").getAsString().equalsIgnoreCase(Type.FAILED.name())) {
            return false;
        } else if (!object.has("response")) {
            return false;
        } else {
            for (@NotNull Response response : Response.values()) {
                if (!object.get("response").getAsString().equalsIgnoreCase(response.name())) {
                    return false;
                }
            }
        }
        return true;
    }

    // Object

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
