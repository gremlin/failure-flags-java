package com.gremlin.failureflags.models;

import java.util.Map;

public class FailureFlag {
    String name;
    Map<String, Object> labels;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Object> labels) {
        this.labels = labels;
    }


}
