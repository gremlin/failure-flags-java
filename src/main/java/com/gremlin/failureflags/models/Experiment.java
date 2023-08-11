package com.gremlin.failureflags.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class Experiment {
    String failureFlagName;
    String guid;
    Double rate;
    Map<String, List<String>> selector;
    Map<String, Object> effect;

    public String getFailureFlagName() {
        return failureFlagName;
    }

    public void setFailureFlagName(String failureFlagName) {
        this.failureFlagName = failureFlagName;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Map<String, List<String>> getSelector() {
        return selector;
    }

    public void setSelector(Map<String, List<String>> selector) {
        this.selector = selector;
    }

    public Map<String, Object> getEffect() {
        return effect;
    }

    public void setEffect(Map<String, Object> effect) {
        this.effect = effect;
    }
}


