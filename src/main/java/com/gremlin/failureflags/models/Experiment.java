package com.gremlin.failureflags.models;

import java.util.List;
import java.util.Map;

public class Experiment {
  String failureFlagName;
  String guid;

  public void setRate(float rate) {
    this.rate = rate;
  }

  float rate;
  Map<String, List<String>> selector;

  public void setEffect(Map<String, Object> effect) {
    this.effect = effect;
  }

  Map<String, Object> effect;

  public String getFailureFlagName() {
    return failureFlagName;
  }

  public String getGuid() {
    return guid;
  }

  public float getRate() {
    return rate;
  }

  public Map<String, List<String>> getSelector() {
    return selector;
  }

  public Map<String, Object> getEffect() {
    return effect;
  }

}
