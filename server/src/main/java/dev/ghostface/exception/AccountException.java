package dev.ghostface.exception;

import javax.naming.AuthenticationException;

public final class AccountException extends AuthenticationException {
    public AccountException(String message) {
        super(message);
    }
}
