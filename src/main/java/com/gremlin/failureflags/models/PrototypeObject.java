package com.gremlin.failureflags.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@Data
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
}
