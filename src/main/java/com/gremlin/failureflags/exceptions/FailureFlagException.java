package com.gremlin.failureflags.exceptions;

public class FailureFlagException extends RuntimeException {
    public FailureFlagException(final String message) {
        super(message);
    }
}
