package com.gremlin.failureflags;

import java.util.Map;

public class FailureFlagException extends RuntimeException {
  public FailureFlagException(final String message) {
    super(message);
  }

}