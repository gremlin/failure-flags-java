package com.gremlin.failureflags;

public interface FailureFlags {
  /**
   * invoke will fetch and apply the provided behaviors for any experiments targeting the provided Failure Flag.
   * */
  Experiment[] invoke(FailureFlag flag, Behavior behavior);
  /**
   * invoke will fetch and apply default behaviors for any experiments targeting the provided Failure Flag.
   * */
  Experiment[] invoke(FailureFlag flag);
  /**
   * fetchExperiment retrieves the list of active experiments targeting the provided Failure Flag.
   *
   * @return null if there are no active experiments targeting the provided Failure Flag.
   * */
  Experiment[] fetch(FailureFlag flag);
}
