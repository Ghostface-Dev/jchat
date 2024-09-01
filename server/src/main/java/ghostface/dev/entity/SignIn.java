package ghostface.dev.entity;

import ghostface.dev.account.Email;
import ghostface.dev.account.Password;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class SignIn {

    private final @NotNull Email email;
    private final @NotNull Password password;

    public SignIn(@NotNull Email email, @NotNull Password password) {
        this.email = email;
        this.password = password;
    }

    public @NotNull Email getEmail() {
        return email;
    }

    public @NotNull Password getPassword() {
        return password;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull SignIn signIn = (SignIn) object;
        return Objects.equals(email, signIn.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

}
