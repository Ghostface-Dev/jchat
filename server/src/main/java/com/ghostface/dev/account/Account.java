package com.ghostface.dev.account;

import com.ghostface.dev.entity.User;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class Account {

    public static final @NotNull Map<@NotNull User, @NotNull String> users = new HashMap<>();


}
