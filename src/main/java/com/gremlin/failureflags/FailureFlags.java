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
   * fetchExperiment retrieves the list of active experiments targeting the provided Failure Flag.
   * @param flag the FailureFlag to invoke
   * @return an array of active Experiments. null if there are no active experiments targeting the provided Failure Flag.
   * */
  Experiment[] fetch(FailureFlag flag);
}
