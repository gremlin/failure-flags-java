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

  public FailureFlag(String name, Map<String, String> labels, boolean debug) {
    this.name = name;
    this.labels = labels;
    this.debug = debug;
  }

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

  public boolean getDebug() { return debug; }
  public void setDebug(boolean debug) { this.debug = debug; }


}
