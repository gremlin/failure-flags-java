package com.gremlin.failureflags;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * FailureFlag represents a named point in code and specific invocation metadata. A Gremlin user can design experiments
 * which target specific Failure Flags.
 * */
public class FailureFlag {
  String name;
  Map<String, String> labels;
  @JsonIgnore
  boolean debug;

  /**
   * Construct a new FailureFlag and set all options.
   *
   * @param name the name of the FailureFlag
   * @param labels the labels of the FailureFlag
   * @param debug the debug state of the FailureFlag
   * */
  public FailureFlag(String name, Map<String, String> labels, boolean debug) {
    this.name = name;
    this.labels = labels;
    this.debug = debug;
  }

  /**
   * getName() returns the name of the FailureFlag.
   * @return the name of the FailureFlag
   * */
  public String getName() {
    return name;
  }

  /**
   * setName() sets the name of the FailureFlag.
   * @param name the name of the FailureFlag
   * */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * getLabels() returns the labels of the FailureFlag.
   * @return the labels of the FailureFlag
   * */
  public Map<String, String> getLabels() {
    return labels;
  }

  /**
   * setLabels() sets the labels of the FailureFlag.
   * @param labels the labels of the FailureFlag
   * */
  public void setLabels(Map<String, String> labels) {
    this.labels = labels;
  }

  /**
   * getDebug() returns true if the FailureFlag is configured for debugging.
   * @return the debug state of the FailureFlag
   * */
  public boolean getDebug() { return debug; }

  /**
   * setDebug() sets the debug state of the FailureFlag.
   * @param debug the debug state of the FailureFlag
   * */
  public void setDebug(boolean debug) { this.debug = debug; }
}
