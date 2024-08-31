package ghostface.dev.packet;

import com.google.gson.JsonObject;
import ghostface.dev.account.Username;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class ServerMessagePacket extends Packet {

    // todo username validation

    public ServerMessagePacket(@NotNull Username username, @NotNull String content, @NotNull OffsetDateTime time) {
        super(new JsonObject(), Type.SERVER_MESSAGE);
        data.addProperty("type", getType().name().toLowerCase());
        data.addProperty("username", username.toString());
        data.addProperty("content", content);
        data.addProperty("time", time.toString());
    }

}
