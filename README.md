# failure-flags

Failure Flags is a Java SDK for building application-level chaos experiments and reliability tests using the Gremlin Fault Injection platform. This library works in concert with Gremlin-Lambda, a Lambda Extension; or Gremlin-Sidecar, a container sidecar agent. This architecture minimizes the impact to your application code, simplifies configuration, and makes adoption painless.

Just like feature flags, Failure Flags are safe to add to and leave in your application. Failure Flags will always fail safe if it cannot communicate with its sidecar or its sidecar is misconfigured.

Take three steps to run an application-level experiment with Failure Flags:

1. Instrument your code with this SDK
2. Configure and deploy your code alongside one of the Failure Flag sidecars
3. Run an Experiment with the console, API, or command line

## Instrumenting Your Code

You can get started by adding failureflags to your package dependencies. In your application's build.gradle, add the following implementation dependency:

```gradle
dependencies {
  implementation 'com.gremlin:failure-flags-java:<version>'
}
```

Under repositories add this maven repository: 

```gradle
maven {
  url 'https://maven.gremlin.com/'
}
```

Then bring in the library and instrument the part of your application where you want to inject faults. 

```java
// Step 1: add the Failure Flags SDK to your dependencies
import com.gremlin.failureflags.*
...

// Step 2: instantiate a FailureFlags instance
FailureFlags gremlin = new GremlinFailureFlags();

// Step 3: call the invoke method at the point in your code where you want to be able to experiment
gremlin.invoke(
    new FailureFlag(    // Pass in an instance of a Failure Flag
        "http-ingress", // Set the name of the flag. This should mean something
                        // to you and the team. These are not special values.

        Map.of(         // Pass in a map of information that describes each
                        // invocation. This is used for experiment targeting.
          // This example passes the HTTP method and path from an HTTP request.
          // Because these have been provided an experiment defined later can 
          // target invocations matching some subset of these labels
          // (IE "method: POST").
          "method", request.getMethod(),
          "path", request.getPath()
        )
    )
);

...
```

The best spots to add a failure flag are just before or just after a call to one of your network dependencies like a database or other network service. Or you can instrument your request handler and affect the way your application responses to its callers. Here's a simple Lambda example:

```java
package com.gremlin.demo.failureflags.demo;

import java.time.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Change #1: Add dependencies on FailureFlags.
import com.gremlin.failureflags.FailureFlags;
import com.gremlin.failureflags.GremlinFailureFlags;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);
    private final FailureFlags gremlin;

    public Handler() {
        // Change #2: Set up an instance of FailureFlags
        gremlin = new GremlinFailureFlags();
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LocalDateTime start = LocalDateTime.now();

        // Change #3: Add a Failure Flag at code points where failures can occur.
        gremlin.invoke(new FailureFlag("http-ingress", Map.of("method", "POST")));

        LocalDateTime end = LocalDateTime.now();
        Duration processingTime = Duration.between(start, end);

        Response responseBody = new Response(processingTime.toMillis());
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(responseBody)
                .setHeaders(headers)
                .build();
    }
}
```

*Don't forget to enable the SDK by setting the FAILURE_FLAGS_ENABLED environment variable!* If this environment variable is not set then the SDK will short-circuit and no attempt to fetch experiments will be made.

You can always bring your own behaviors and effects by providing a behavior function. Here's another Lambda example that writes the experiment data to the console instead of changing the application behavior:

```java
package com.gremlin.demo.failureflags.demo;

import java.time.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Change #1: Add dependencies on FailureFlags.
import com.gremlin.failureflags.FailureFlags;
import com.gremlin.failureflags.GremlinFailureFlags;
import com.gremlin.failureflags.Experiment;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);
    private final FailureFlags gremlin;

    public Handler() {
        // Change #2: Set up an instance of FailureFlags
        gremlin = new GremlinFailureFlags();
    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LocalDateTime start = LocalDateTime.now();

        // Change #3: Add a Failure Flag at code points where failures can occur.
        gremlin.invoke(
                new FailureFlag("http-ingress", Map.of("method", "POST")),
                // Define custom behaviors inline by implementing the Behavior functional interface.
                (experiments)->{
                    LOGGER.info("retrieved {} experiments", experiments.size());
                    for (Experiment e: experiments) {
                        LOGGER.info("retrieved {}", e.getGuid());
                    }
                    return gremlin.getDefaultBehavior().applyBehavior(experiments);
                });

        LocalDateTime end = LocalDateTime.now();
        Duration processingTime = Duration.between(start, end);

        Response responseBody = new Response(processingTime.toMillis());
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return ApiGatewayResponse.builder()
                .setStatusCode(200)
                .setObjectBody(responseBody)
                .setHeaders(headers)
                .build();
    }
}
```

### Doing Something Different

Sometimes you need even more manual control. For example, in the event of an experiment you might not want to make some API call or need to rollback some transaction. In most cases the Exception effect can help, but the `invoke` function also returns a boolean to indicate if there was an experiment. You can use that to create branches in your code like you would for any feature flag.

```java
...
Experiments[] activeExperiments = gremlin.invoke(new FailureFlag("http-ingress", Map.of("method", "POST")));
if (activeExperiments != null && activeExperiments.size() > 0){
    // if there is a running experiment then do this
} else{
    // if there is no experiment then do this
}
...
```

### Pulling the Experiment and Branching Manually

If you want to work with lower-level Experiment data you can use `fetch` directly.

## Targeting with Selectors

Experiments match specific invocations of a Failure Flag based on its name, and the labels you provide. Experiments define Selectors that the Failure Flags engine uses to determine if an invocation matches. Selectors are simple key to list of values maps. The basic matching logic is every key in a selector must be present in the Failure Flag labels, and at least one of the values in the list for a selector key must match the value in the label.

## Effects and Examples

Once you've instrumented your code and deployed your application with the sidecar you're ready to run an Experiment. None of the work you've done so far describes the Effect during an experiment. You've only marked the spots in code where you want the opportunity to experiment. Gremlin Failure Flags Experiments take an Effect parameter. The Effect parameter is a simple JSON map. That map is provided to the Failure Flags SDK if the application is targeted by a running Experiment. The Failure Flags SDK will process the map according to the default behavior chain or the behaviors you've provided. Today the default chain provides both latency and error Effects.

### Introduce Flat Latency

This Effect will introduce a constant 2000-millisecond delay.

```json
{ "latency": 2000 }
```

### Introduce Minimum Latency with Some Maximum Jitter

This Effect will introduce between 2000 and 2200 milliseconds of latency where there is a pseudo-random uniform probability of any delay between 2000 and 2200.

```json
{
  "latency": {
    "ms": 2000,
    "jitter": 200
  }
}
```

### Throw an Exception

This Effect will cause Failure Flags to throw an unchecked FailureFlagException with the provided message.

```json
{ "exception": "this is a custom message" }
```

### Combining the Two for a "Delayed Exception"

Many common failure modes eventually result in an exception being thrown, but there will be some delay before that happens. Examples include network connection failures, or degradation, or other timeout-based issues.

This Effect Statement will cause a Failure Flag to pause for a full 2 seconds before throwing an exception/error a message, "Custom TCP Timeout Simulation"

```json
{
  "latency": 2000,
  "exception": "Custom TCP Timeout Simulation"
}
```

### Inject Synthetic Response Data

The `data` effect replaces the return value of the instrumented call with whatever you configure in the experiment. This is useful for simulating degraded responses, empty result sets, throttling replies, or any other scenario where the response content matters more than latency or exceptions.

```json
{ "data": { "status": "THROTTLED", "retryAfter": 30 } }
```

To use this effect your instrumentation must use the `BehaviorWithEffect` form of `invoke` described below.

### Advanced: Providing Metadata to Custom Behaviors

The default effect chain included with the Failure Flags SDK is aware of well-known effect properties including, "latency" and "exception." The user can extend or replace that functionality and use the same properties, or provide their own. For example, suppose a user wants to use a "random jitter" effect that the Standard Chain does not provide. Suppose they wanted to inject a random amount of jitter up to some maximum. They could implement that small extension and make up their own Effect property called, "my-jitter" that specifies that maximum. The resulting Effect Statement would look like:

```json
{ "my-jitter": 500 }
```

They might also combine this with parts of the default chain:

```json
{
  "latency": 1000,
  "my-jitter": 500
}
```

## Mutating Return Values

The standard `invoke` form applies side-effect behaviors like latency and exceptions but cannot change what a method returns. When you need to inject a synthetic response — replacing what a downstream call returns rather than just delaying or crashing it — use the `BehaviorWithEffect` form of `invoke`.

This form takes the original return value, applies the experiment's behavior, and returns either the replacement supplied by the behavior or the original if no mutation was requested.

### Using the Built-in `DataEffect`

`DataEffect` reads `effect.data` from the active experiment and converts it to your target type using Jackson. Pass it the expected return type and it handles the rest.

```java
import com.gremlin.failureflags.FailureFlags;
import com.gremlin.failureflags.GremlinFailureFlags;
import com.gremlin.failureflags.FailureFlag;
import com.gremlin.failureflags.behaviors.DataEffect;

FailureFlags gremlin = new GremlinFailureFlags();

// Call the downstream dependency as normal.
QueryResponse response = dynamoDb.query(request);

// Pass the result through invoke. If a matching experiment is running with an
// effect.data payload, response is replaced with the synthetic value.
// Otherwise it is returned unchanged.
response = gremlin.invoke(
    new FailureFlag("dynamo-query", Map.of("table", tableName)),
    response,
    new DataEffect<>(QueryResponse.class));
```

The experiment effect JSON for this example would look like:

```json
{
  "data": {
    "count": 0,
    "items": []
  }
}
```

### Writing a Custom `BehaviorWithEffect`

`BehaviorWithEffect<T>` is a functional interface. Return `Optional.of(replacement)` to swap the value, or `Optional.empty()` to leave it unchanged.

```java
response = gremlin.invoke(
    new FailureFlag("dynamo-query", Map.of("table", tableName)),
    response,
    (experiments, original) -> {
        // Inspect the experiments and decide whether to mutate.
        for (Experiment e : experiments) {
            if (e.getEffect().containsKey("emptyResult")) {
                return Optional.of(QueryResponse.builder().count(0).build());
            }
        }
        return Optional.empty(); // leave original unchanged
    });
```
