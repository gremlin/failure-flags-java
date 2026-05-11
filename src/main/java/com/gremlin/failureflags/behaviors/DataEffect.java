package com.gremlin.failureflags.behaviors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gremlin.failureflags.BehaviorWithEffect;
import com.gremlin.failureflags.Experiment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * DataEffect is a built-in {@link BehaviorWithEffect} that replaces the original value with the contents of
 * {@code effect.data} from the first matching experiment that carries it. Jackson is used to coerce the raw map/value
 * from the experiment into the target type, so nested structures are handled automatically.
 *
 * <p>Example experiment effect JSON:
 * <pre>
 * { "data": { "status": "throttled", "retryAfter": 30 } }
 * </pre>
 *
 * @param <T> the expected type of the mutated return value
 */
public class DataEffect<T> implements BehaviorWithEffect<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataEffect.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final Class<T> type;

  /**
   * Construct a DataEffect that will convert {@code effect.data} to the given type.
   *
   * @param type the class to convert the effect data into
   */
  public DataEffect(Class<T> type) {
    if (type == null) {
      throw new IllegalArgumentException("type must not be null");
    }
    this.type = type;
  }

  @Override
  public Optional<T> applyBehavior(Experiment[] experiments, T original) {
    for (Experiment experiment : experiments) {
      Map<String, Object> effect = experiment.getEffect();
      if (effect == null || !effect.containsKey("data")) {
        continue;
      }
      Object data = effect.get("data");
      try {
        T converted = MAPPER.convertValue(data, type);
        return Optional.of(converted);
      } catch (IllegalArgumentException e) {
        LOGGER.warn("DataEffect: could not convert effect.data to {}: {}", type.getName(), e.getMessage());
      }
    }
    return Optional.empty();
  }
}
