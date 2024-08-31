package ghostface.dev.packet;

import com.google.gson.JsonObject;
import ghostface.dev.packet.impl.*;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;

public abstract class Packet {

    public static @NotNull Packet get(@NotNull JsonObject json) {
        @NotNull String getType = json.get("type").getAsString();

        if (getType.equals("authentication") && AuthPacket.validate(json)) {
            return new AuthPacket(json.get("email").getAsString(), json.get("password").getAsString());
        } else if (getType.equals("failed") && FailedPacket.validate(json)) {
            return new FailedPacket(FailedPacket.Response.valueOf(json.get("response").getAsString()));
        } else if (getType.equals("message") && MessagePacket.validate(json)) {
            return new MessagePacket(json.get("content").getAsString(), OffsetDateTime.parse(json.get("time").getAsString()));
        } else if (getType.equals("registry") && RegistryPacket.validate(json)) {
            return new RegistryPacket(json.get("username").getAsString(), json.get("email").getAsString(), json.get("password").getAsString());
        } else if (getType.equals("server_message") && ServerMessagePacket.validate(json)) {
            return new ServerMessagePacket(json.get("username").getAsString(), json.get("content").getAsString(), json.get("time").getAsString());
        }
        throw new IllegalArgumentException("Fields is missing or incorrect");
    }

    protected final @NotNull JsonObject data;
    protected final @NotNull Type type;

    protected Packet( @NotNull JsonObject data, @NotNull Type type) {
        this.data = data;
        this.type = type;
    }

    // Getters

    public final @NotNull Type getType() {
        return type;
    }

    public enum Type {
        AUTHENTICATION,
        MESSAGE,
        REGISTRY,
        FAILED,
        SERVER_MESSAGE
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
