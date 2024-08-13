package com.ghostface.dev.factory;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.OffsetDateTime;

public interface User extends Serializable {

    @NotNull String getUsername();

    @NotNull OffsetDateTime getCreation();

}
