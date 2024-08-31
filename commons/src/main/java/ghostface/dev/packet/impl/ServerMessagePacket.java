package ghostface.dev.packet.impl;

import com.google.gson.JsonObject;
import ghostface.dev.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class ServerMessagePacket extends Packet {

    // todo username validation
    public static boolean validate(@NotNull JsonObject object) {
        if (!object.has("type") || !object.get("type").getAsString().equalsIgnoreCase(Type.SERVER_MESSAGE.name())) {
            return false;
        } else if (!object.has("username")) {
            return false;
        } else if (!object.has("content")) {
            return false;
        } else if (!object.has("time")) {
            return false;
        } else try {
            OffsetDateTime.parse(object.get("time").getAsString());
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public ServerMessagePacket(@NotNull String username, @NotNull String content, @NotNull String time) {
        super(new JsonObject(), Type.SERVER_MESSAGE);
        data.addProperty("type", getType().name().toLowerCase());
        data.addProperty("username", username);
        data.addProperty("content", content);
        data.addProperty("time", time);
    }

}
