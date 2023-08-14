package com.gremlin.failureflags.models;

import java.util.List;
import java.util.Map;

public class ExperimentPayload {
  String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, String> getLabels() {
    return labels;
  }

  public void setLabels(Map<String, String> labels) {
    this.labels = labels;
  }

  Map<String, String> labels;
}
