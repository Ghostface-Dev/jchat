package com.ghostface.dev.factory;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;

public interface Message extends Serializable {

    @NotNull String getContent();

    @NotNull User getUser();

    @NotNull OffsetDateTime getDate();

}
