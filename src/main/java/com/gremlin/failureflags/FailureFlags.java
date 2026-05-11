package com.gremlin.failureflags;

/**
 * FailureFlags is an interface exposing the core functionality of the FailureFlags system. Code to this interface to
 * improve testability of your code. GremlinFailureFlags is the default implementation.
 * */
public interface FailureFlags {
  /**
   * invoke will fetch and apply the provided behaviors for any experiments targeting the provided Failure Flag.
   * @param flag the FailureFlag to invoke
   * @param behavior the specific or inline behavior to use for any active experiments
   * @return an array of active Experiments. null if there are no active experiments targeting the provided Failure Flag.
   * */
  Experiment[] invoke(FailureFlag flag, Behavior behavior);
  /**
   * invoke will fetch and apply default behaviors for any experiments targeting the provided Failure Flag.
   * @param flag the FailureFlag to invoke
   * @return an array of active Experiments. null if there are no active experiments targeting the provided Failure Flag.
   * */
  Experiment[] invoke(FailureFlag flag);
  /**
   * invoke fetches experiments targeting the provided Failure Flag, applies the given {@link BehaviorWithEffect}, and
   * returns the original value or a replacement supplied by the behavior. Use this form when you need to mutate the
   * return value of an instrumented call (e.g. injecting synthetic response data via {@code effect.data}).
   *
   * <p>When the SDK is disabled or no experiments are active the original value is returned unchanged.
   *
   * @param flag     the FailureFlag to invoke
   * @param original the value to potentially replace
   * @param behavior the behavior that may return a replacement value
   * @param <T>      the type of the value
   * @return the replacement value supplied by the behavior, or {@code original} if none was provided
   */
  default <T> T invoke(FailureFlag flag, T original, BehaviorWithEffect<T> behavior) {
    return original;
  }
  /**
   * fetchExperiment retrieves the list of active experiments targeting the provided Failure Flag.
   * @param flag the FailureFlag to invoke
   * @return an array of active Experiments. null if there are no active experiments targeting the provided Failure Flag.
   * */
  Experiment[] fetch(FailureFlag flag);
}
