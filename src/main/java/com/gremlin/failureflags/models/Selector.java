package com.gremlin.failureflags.models;

import java.util.List;
import java.util.Map;

public class Selector {
    String name;
    Map<String, List> labels;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, List> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, List> labels) {
        this.labels = labels;
    }
}
