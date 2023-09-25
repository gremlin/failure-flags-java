package com.gremlin.failureflags;

/**
 * Behaviors implement specific effects or symptoms of failures that an application will experience in calls to
 * FailureFlags.invoke(...). When processing multiple experiments, delays should be applied before other failure types
 * and those failure types that can be processed without changing flow should be applied first. If multiple experiments
 * result in changing control flow (like exceptions, shutdowns, panics, etc.) then the behavior chain may not realize
 * some effects.
 *
 * This is a functional interface. An adopter might use Lambdas to provide behavior inline.
 * */
@FunctionalInterface
public interface Behavior {
  /**
   * applyBehavior applies any behavior described by the effect statements in each experiment in the provided array.
   *
   * @param experiments an ordered array of active Experiments to apply
   * */
  void applyBehavior(Experiment[] experiments);
}
