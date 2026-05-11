package com.gremlin.failureflags;

import java.util.Optional;

/**
 * BehaviorWithEffect is a generic variant of {@link Behavior} that can transform the return value of an instrumented
 * method call. Use this when you need to inject data mutations (via {@code effect.data}) in addition to, or instead of,
 * side-effect-only behaviors like latency and exceptions.
 *
 * <p>The {@code applyBehavior} method receives the original return value and the active experiments. It should return
 * {@code Optional.of(newValue)} to replace the original, or {@code Optional.empty()} to leave it unchanged.
 *
 * <p>This is a functional interface. An adopter might use lambdas to provide behavior inline.
 *
 * @param <T> the type of the value to potentially mutate
 */
@FunctionalInterface
public interface BehaviorWithEffect<T> {
  /**
   * applyBehavior applies any behavior described by the effect statements in each experiment in the provided array,
   * optionally returning a replacement for the original value.
   *
   * @param experiments an ordered array of active Experiments to apply
   * @param original    the original value returned by the instrumented call
   * @return {@code Optional.of(replacement)} if the value should be mutated, {@code Optional.empty()} otherwise
   */
  Optional<T> applyBehavior(Experiment[] experiments, T original);
}
