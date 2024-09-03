package ghostface.dev.util;

import com.google.gson.JsonObject;
import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import ghostface.dev.account.Username;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class PacketValidator {

    public static boolean isAuthentication(@NotNull JsonObject object) {
        if (!object.has("type") || !object.get("type").getAsString().equalsIgnoreCase((Type.AUTHENTICATION.name()))) {
            return false;
        } else if (!object.has("email") || !Email.isValid(object.get("email").getAsString())) {
            return false;
        } else if (!object.has("password") || Password.isValid(object.get("password").getAsString())) {
            return false;
        }
        return true;
    }

    public static boolean isMessage(@NotNull JsonObject object) {
        if (!object.has("type") || !object.get("type").getAsString().equalsIgnoreCase(Type.MESSAGE.name())) {
            return false;
        } else if (!object.has("text")) {
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

    public static boolean isRegistry(@NotNull JsonObject object) {
        if (!object.has("type") || !object.get("type").getAsString().equalsIgnoreCase(Type.REGISTRY.name())) {
            return false;
        } else if (!object.has("email") || !Email.isValid(object.get("email").getAsString())) {
            return false;
        } else if (!object.has("username") || !Username.isValid(object.get("username").getAsString())) {
            return false;
        } else if (!object.has("password") || Password.isValid(object.get("password").getAsString())) {
            return false;
        }
        return true;
    }

    public enum Type {
        AUTHENTICATION,
        MESSAGE,
        REGISTRY,
    }

    private PacketValidator() {
        throw new UnsupportedOperationException();
    }

}
