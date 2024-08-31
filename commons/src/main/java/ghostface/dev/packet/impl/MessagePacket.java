package ghostface.dev.packet.impl;

import com.google.gson.JsonObject;
import ghostface.dev.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public final class MessagePacket extends Packet {

    public static boolean validate(@NotNull JsonObject object) {
        if (!object.has("type") || !object.get("type").getAsString().equalsIgnoreCase(Type.MESSAGE.name())) {
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

    public MessagePacket(@NotNull String content, @NotNull OffsetDateTime time) {
        super(new JsonObject() , Type.MESSAGE);
        data.addProperty("type", getType().name().toLowerCase());
        data.addProperty("content", content);
        data.addProperty("time", time.toString());
    }

}
