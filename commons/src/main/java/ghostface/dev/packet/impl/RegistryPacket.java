package ghostface.dev.packet.impl;

import com.google.gson.JsonObject;
import ghostface.dev.packet.Packet;
import org.jetbrains.annotations.NotNull;

public final class RegistryPacket extends Packet {

    // todo email validation
    // todo username valdiation
    // todo password validation
    public static boolean validate(@NotNull JsonObject object) {
        if (!object.has("type") || !object.get("type").getAsString().equalsIgnoreCase(Type.REGISTRY.name())) {
            return false;
        } else if (!object.has("email")) {
            return false;
        } else if (!object.has("username")) {
            return false;
        } else if (!object.has("password")) {
            return false;
        }
        return true;
    }

    // todo replace email string to email class
    // todo replace username string to class
    // todo replace password string to class
    public RegistryPacket(@NotNull String username, @NotNull String email, @NotNull String password) {
        super(new JsonObject(), Type.REGISTRY);
        data.addProperty("type", getType().name().toLowerCase());
        data.addProperty("email", email);
        data.addProperty("username", username);
        data.addProperty("password", password);
    }

}
