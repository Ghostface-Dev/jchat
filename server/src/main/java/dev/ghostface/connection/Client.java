package dev.ghostface.connection;

import codes.ghostface.ClientPacket;
import codes.ghostface.ServerPacket;
import dev.ghostface.server.JChatServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.AuthenticationException;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.Objects;
import java.util.Optional;

public final class Client {

    private final @NotNull Socket socket;
    private final @NotNull JChatServer server;

    public Client(@NotNull Socket socket, @NotNull JChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public @Nullable ClientPacket read() throws IOException, ClassNotFoundException {
        @NotNull ByteBuffer buffer = ByteBuffer.allocate(2048);
        @NotNull ByteArrayOutputStream array = new ByteArrayOutputStream();

        buffer.clear();
        int readyBytes = socket.getChannel().read(buffer);

        if (readyBytes == -1) {
            throw new ClosedChannelException();
        } else if (readyBytes == 0) {
            return null;
        } else while (readyBytes > 0) {
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            array.write(bytes);
            buffer.clear();
            readyBytes = socket.getChannel().read(buffer);
        }

        @NotNull ByteArrayInputStream arrayIn = new ByteArrayInputStream(array.toByteArray());
        @NotNull ObjectInputStream obj = new ObjectInputStream(arrayIn);
        arrayIn.close();

        return (ClientPacket) obj.readObject();
    }

    public void write(@NotNull ServerPacket packet) throws ClosedChannelException {
        try {
            this.socket.getChannel().write(ByteBuffer.wrap(packet.toString().getBytes()));
        } catch (IOException e) {
            throw new ClosedChannelException();
        }
    }

    public void close() {
        getServer().getClients().remove(this);
        try {
            deauthenticate();
            getSocket().close();
        } catch (AuthenticationException | IOException ignore) {}
    }

    public void deauthenticate() throws AuthenticationException {
        @NotNull Optional<@NotNull Account> optionalAccount = Data.getInstance().getAccount(this);

        if (!isAuthenticated() || !optionalAccount.isPresent()) {
            throw new AuthenticationException("Client already deauthenticated");
        }

        optionalAccount.get().setClient(null);
    }

    public boolean isAuthenticated() {
        return Data.getInstance().getAccount(this).isPresent();
    }

    // Getters

    public @NotNull Socket getSocket() {
        return socket;
    }

    public @NotNull JChatServer getServer() {
        return server;
    }

    // Native

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        @NotNull Client client = (Client) object;
        return Objects.equals(socket, client.socket);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(socket);
    }
}
