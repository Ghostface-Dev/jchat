package com.ghostface.dev.connection;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class JChatClients implements Collection<@NotNull JChatClient> {

    private final @NotNull Set<@NotNull JChatClient> clients = ConcurrentHashMap.newKeySet();


    @Override
    public int size() {
        return clients.size();
    }

    @Override
    public boolean isEmpty() {
        return clients.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return clients.contains(o);
    }

    @Override
    public @NotNull Iterator<JChatClient> iterator() {
        return clients.iterator();
    }

    @Override
    public  @NotNull Object @NotNull [] toArray() {
        return clients.toArray();
    }


    @Override
    public @NotNull <T> T @NotNull [] toArray(@NotNull T[] a) {
        return clients.toArray(a);
    }

    @Override
    public boolean add(@NotNull JChatClient jChatClient) {
        return clients.add(jChatClient);
    }

    @Override
    public boolean remove(Object o) {
        return clients.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return clients.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends @NotNull JChatClient> c) {
        return clients.addAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return clients.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return clients.retainAll(c);
    }

    @Override
    public void clear() {
        clients.clear();
    }

}
