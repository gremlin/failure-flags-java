package com.gremlin.failureflags.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@NoArgsConstructor
@AllArgsConstructor
public class PrototypeObject {
    Map<String, Object> latency;
    Map<String, Object> exception;
    Map<String, Object> data;
    public void copyProperties(PrototypeObject other) {
        this.latency = other.latency;
        this.exception = other.exception;
        this.data = other.data;
    }

    public Map<String, Object> getLatency() {
        return latency;
    }

    public void setLatency(Map<String, Object> latency) {
        this.latency = latency;
    }

    public Map<String, Object> getException() {
        return exception;
    }

    public void setException(Map<String, Object> exception) {
        this.exception = exception;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
