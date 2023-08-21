package com.gremlin.failureflags.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Experiment {
  String name;
  String guid;
  float rate;
  Map<String, Object> effect;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public float getRate() {
    return rate;
  }

  public void setRate(float rate) {
    this.rate = rate;
  }

  public Map<String, Object> getEffect() {
    return effect;
  }

  public void setEffect(Map<String, Object> effect) {
    this.effect = effect;
  }

}
