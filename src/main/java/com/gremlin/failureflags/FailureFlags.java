package com.gremlin.failureflags;

import com.gremlin.failureflags.models.Experiment;
import java.util.Map;

public interface FailureFlags {

  Experiment ifExperimentActive(String name, Map<String, String> labels, Behavior behavior, boolean debug);

  Experiment fetchExperiment(String name, Map<String, String> labels, boolean debug);

}
