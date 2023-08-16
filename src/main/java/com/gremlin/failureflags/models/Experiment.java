package com.gremlin.failureflags.models;


import java.util.Map;

public class Experiment {
    String name;
    String guid;
    float rate;
    Selector selector;
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
    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public Map<String, Object> getEffect() {
        return effect;
    }

    public void setEffect(Map<String, Object> effect) {
        this.effect = effect;
    }
}


