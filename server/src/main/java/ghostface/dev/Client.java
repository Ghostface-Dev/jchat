package ghostface.dev;

import ghostface.dev.server.ServerJChat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public final class Client {

    private final @NotNull ServerJChat chat;
    private final @NotNull SocketChannel channel;

    public Client(@NotNull ServerJChat chat, @NotNull SocketChannel channel) {
        this.chat = chat;
        this.channel = channel;
    }

    public void close() throws ClosedChannelException {
        try {
            channel.close();
            chat.getClients().remove(this);
        } catch (IOException e) {
            throw new ClosedChannelException();
        }
    }

    public @Nullable String read(@NotNull SelectionKey key) throws ClosedChannelException {
        @NotNull SocketChannel channel = (SocketChannel) key.channel();

        @NotNull ByteBuffer buffer = ByteBuffer.allocate(4096); // 4KB
        @NotNull StringBuilder builder = new StringBuilder();
        buffer.clear();

        try {
            @Range(from = 0, to = Integer.MAX_VALUE)
            int response = channel.read(buffer);

            if (response == -1) {
                throw new IOException();
            } else if (response == 0) {
                return null;
            } else while (response > 0) {
                buffer.flip();
                builder.append(StandardCharsets.UTF_8.decode(buffer));
                buffer.clear();
                response = channel.read(buffer);
            }
            return builder.toString();
        } catch (IOException e) {
            throw new ClosedChannelException();
        }
    }

    // Getters


    public @NotNull SocketChannel getChannel() {
        return channel;
    }
}
