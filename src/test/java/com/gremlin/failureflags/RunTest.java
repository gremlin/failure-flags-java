package com.gremlin.failureflags;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.gremlin.failureflags.models.Experiment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RunTest {
    private Fetch fetch;
    private  Fault fault;
    private Map<String, Map<String, Object>> requests;
    @BeforeEach
    public void setUp(){
        fetch = Mockito.mock(Fetch.class);
        fault = Mockito.mock(Fault.class);

        requests = new HashMap<>();
        Map<String, Object> customRequest = new HashMap<>();
        customRequest.put("name", "custom");
        Map<String, String> labels = new HashMap<>();
        labels.put("a", "1");
        labels.put("b", "2");
        customRequest.put("labels", labels);
        requests.put("custom", customRequest);

    }
    @Test
    public void ifExperiment_doesNothing_IfCallbackNotFunction() throws IOException {
        //Set up response
        Experiment experiment = new Experiment();
        experiment.setGuid("6884c0df-ed70-4bc8-84c0-dfed703bc8a7");
        experiment.setFailureFlagName("custom");
        experiment.setRate(0.5);
        Map<String, List<String>> selector = new HashMap<>();
        selector.put("a", Collections.singletonList(("1")));
        selector.put("b", Collections.singletonList("2"));
        experiment.setSelector(selector);
        Map<String, Object> effect = new HashMap<>();
        effect.put("latency-flat", "10");
        experiment.setEffect(effect);

        Mockito.when(Fetch.fetchExperiment(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(experiment);



    }



}