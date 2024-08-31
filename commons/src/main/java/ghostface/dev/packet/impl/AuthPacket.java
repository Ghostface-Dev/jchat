package ghostface.dev.packet.impl;

import com.google.gson.JsonObject;
import ghostface.dev.packet.Packet;
import org.jetbrains.annotations.NotNull;

public final class AuthPacket extends Packet {

    // todo: replace email string to email class
    // todo: email validation
    public static boolean validate(@NotNull JsonObject object) {
        if (!object.has("type") || !object.get("type").getAsString().equalsIgnoreCase((Type.AUTHENTICATION.name()))) {
            return false;
        } else if (!object.has("email")) {
            return false;
        } else if (!object.has("password")) {
            return false;
        }
        return true;
    }

    public AuthPacket(@NotNull String email, @NotNull String password) {
        super(new JsonObject(), Type.AUTHENTICATION);
        data.addProperty("type", getType().name().toLowerCase());
        data.addProperty("email", email);
        data.addProperty("password", password);
    }

}
