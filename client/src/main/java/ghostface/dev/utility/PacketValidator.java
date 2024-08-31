package ghostface.dev.utility;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public final class PacketValidator {

    public static boolean isServerMessage(@NotNull JsonObject object) {
        if (!object.has("type") || !object.get("type").getAsString().equalsIgnoreCase(Type.SERVER_MESSAGE.name())) {
            return false;
        } else if (!object.has("username")) {
            return false;
        } else if (!object.has("content")) {
            return false;
        } else if (!object.has("time")) {
            return false;
        }
        return true;
    }

    public static boolean isFailed(@NotNull JsonObject object) {
        if (!object.has("type") || !object.get("type").getAsString().equalsIgnoreCase(Type.FAILED.name())) {
            return false;
        } else if (!object.has("response")) {
            return false;
        } else {
            for (@NotNull Response response : Response.values()) {
                if (!object.get("response").getAsString().equalsIgnoreCase(response.name())) {
                    return false;
                }
            }
        }
        return true;
    }

    public enum Type {
        FAILED,
        SERVER_MESSAGE
    }

    public enum Response {
        NOT_FOUND,
        EXISTING_USERNAME,
        EXISTING_EMAIL,
        DISCONNECT,
    }

}
