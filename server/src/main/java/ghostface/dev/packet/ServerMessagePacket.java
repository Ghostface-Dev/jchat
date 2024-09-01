package ghostface.dev.packet;

import com.google.gson.JsonObject;
import ghostface.dev.account.Username;
import ghostface.dev.entity.Message;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public class ServerMessagePacket extends Packet {

    public ServerMessagePacket(@NotNull Message message) {
        super(new JsonObject(), Type.SERVER_MESSAGE);
        data.addProperty("type", getType().name().toLowerCase());
        data.addProperty("username", message.getUsername().toString());
        data.addProperty("content", message.getText());
        data.addProperty("time", message.getTime().toString());
    }

}
