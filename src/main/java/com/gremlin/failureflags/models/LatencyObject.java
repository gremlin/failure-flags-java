package com.gremlin.failureflags.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
public class LatencyObject {
    Integer ms;
    Integer jitter;

    public Integer getMs() {
        return ms;
    }

    public void setMs(Integer ms) {
        this.ms = ms;
    }

    public Integer getJitter() {
        return jitter;
    }

    public void setJitter(Integer jitter) {
        this.jitter = jitter;
    }
}
