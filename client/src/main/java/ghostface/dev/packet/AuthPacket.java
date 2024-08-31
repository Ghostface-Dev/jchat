package ghostface.dev.packet;

import com.google.gson.JsonObject;
import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import org.jetbrains.annotations.NotNull;

public final class AuthPacket extends Packet {

    public AuthPacket(@NotNull Email email, @NotNull Password password) {
        super(new JsonObject(), Type.AUTHENTICATION);
        data.addProperty("type", getType().name().toLowerCase());
        data.addProperty("email", email.toString());
        data.addProperty("password", password.toString());
    }

}
