package com.gremlin.failureflags;

import java.util.Map;

/**
 * FailureFlagException is an unchecked exception thrown by the Exception behavior while processing an Experiment with
 * an <code>exception</code> property on its effect statement.
 * */
public class FailureFlagException extends RuntimeException {
  /**
   * Construct a new FailureFlagException.
   * @param message the message to attach to this exception.
   * */
  public FailureFlagException(final String message) {
    super(message);
  }
}