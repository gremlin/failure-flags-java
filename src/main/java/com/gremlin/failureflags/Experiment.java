package com.gremlin.failureflags;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

/**
 * Experiment describes an active experiment defined by a Gremlin user.
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Experiment {
  String name;
  String guid;
  float rate;
  Map<String, Object> effect;
  /**
   * getName() returns the name of the experiment.
   * @return the name of the experiment
   * */
  public String getName() {
    return name;
  }

  /**
   * setName() sets the name of the experiment.
   * @param name the name of the experiment
   * */
  public void setName(String name) {
    this.name = name;
  }
  /**
   * getGuid() returns the GUID of the experiment.
   * @return the GUID of the experiment
   * */
  public String getGuid() {
    return guid;
  }
  /**
   * setGuid() sets the GUID of the experiment.
   * @param guid the guid of the experiment
   * */
  public void setGuid(String guid) {
    this.guid = guid;
  }
  /**
   * getRate() returns the rate of the experiment.
   * @return the rate of the experiment
   * */
  public float getRate() {
    return rate;
  }
  /**
   * setRate() sets the rate of the experiment.
   * @param rate the rate of the experiment
   * */
  public void setRate(float rate) {
    this.rate = rate;
  }
  /**
   * getEffect() returns the effect of the experiment.
   * @return the effect of the experiment
   * */
  public Map<String, Object> getEffect() {
    return effect;
  }
  /**
   * setEffect() sets the effect of the experiment.
   * @param effect the effect of the experiment
   * */
  public void setEffect(Map<String, Object> effect) {
    this.effect = effect;
  }
}
