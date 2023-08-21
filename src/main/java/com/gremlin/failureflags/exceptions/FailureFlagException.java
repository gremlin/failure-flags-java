package com.gremlin.failureflags.exceptions;

import java.util.Map;

public class FailureFlagException extends RuntimeException {
  public FailureFlagException(final String message) {
    super(message);
  }

}