package ghostface.dev.packet;

import com.google.gson.JsonObject;
import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import ghostface.dev.account.Username;
import org.jetbrains.annotations.NotNull;

public final class RegistryPacket extends Packet {

    public RegistryPacket(@NotNull Username username, @NotNull Email email, @NotNull Password password) {
        super(new JsonObject(), Type.REGISTRY);
        data.addProperty("type", getType().name().toLowerCase());
        data.addProperty("email", email.toString());
        data.addProperty("username", username.toString());
        data.addProperty("password", password.toString());
    }

}
