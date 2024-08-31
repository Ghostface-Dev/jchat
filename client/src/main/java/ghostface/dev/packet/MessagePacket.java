package ghostface.dev.packet;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public final class MessagePacket extends Packet {

    public MessagePacket(@NotNull String content, @NotNull OffsetDateTime time) {
        super(new JsonObject() , Type.MESSAGE);
        data.addProperty("type", getType().name().toLowerCase());
        data.addProperty("content", content);
        data.addProperty("time", time.toString());
    }

}
