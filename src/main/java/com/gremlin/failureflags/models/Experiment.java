package com.gremlin.failureflags.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Experiment {
    String failureFlagName;
    String guid;
    Double rate;
    Map<String, List<String >> selector;
    Map<String, Object> effect;
}


