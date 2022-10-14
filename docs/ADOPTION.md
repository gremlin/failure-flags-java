# Adopting Failure Flags

In your application's build.gradle, add the following dependency

```bash
dependencies {
  implementation 'com.gremlin:failure-flags-java:<version>'
}
```

Under repositories add this maven repository

```bash
maven {
  url 'https://maven.gremlin.com/'
}
```

The code below is what it might look like to adopt a minimal Failure Flags framework.     


```java
// Create an agent in your application
// The GremlinAgent with auto detect all of these attributes, 
// but the adopter can override them via builder.
FailureFlags gremlin = new GremlinAgent.Builder()
    .withIdentifier(...)       // required. omit to autoconfigure from /etc/gremlin/config.yaml
    .withTeamId(...)           // required. omit to autoconfigure from /etc/gremlin/config.yaml
    .withTeamSecret(...)       // required for secret based auth. omit to autoconfigure from /etc/gremlin/config.yaml
    .withTeamCertificate(...)  // requird for cert based auth. omit to autoconfigure from /etc/gremlin/config.yaml
    .withTeamKey(...)          // required for cert based auth, omit to autoconfigure from /etc/gremlin/config.yaml
    .withRegion(...)           // omit to autoconfigure
    .withZone(...)             // omit to autoconfigure
    .withStage(...)            // omit to autoconfigure
    .withVersion(...)          // omit to autoconfigure
    .withBuild(...)            // omit to autoconfigure
    .withTagPair(..., ...)     // omit to autoconfigure
    .buildMock(true);          // false to always return false 

...

// Throw some application-defined exception for calls to a specific dependency
if(gremlin.isDependencyTestActive(DEPENDENCY_NAME, GremlinTests.Availability)) {
    // If the dependency is under an Availability test
    throw new ServiceUnavailableException();
} else if (gremlin.isDependencyTestActive(DEPENDENCY_NAME, GremlinTests.ExpiredCertificate) {
    // If the dependency is under a certificate expiration test
    // A.K.A. How do we react when this dependency's SSL certificate expires?
    throw new javax.net.ssl.SSLHandshakeException("PKIX path validation failed: java.security.cert.CertPathValidatorException: validity check failed");
} else {
    HttpClient client = HttpClient.newBuilder()
        .version(Version.HTTP_1_1)
        .followRedirects(Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))
        .authenticator(Authenticator.getDefault())
        .build();
    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    ...
}

...

// Inject latency for calls to a path-based dependency
if gremlin.isDependencyTestActive(DEPENDENCY_ENDPOINT_URL, GremlinTests.Latency) {
    Gremlins.Latency(ONE_SECOND);
}
... regular client code

```

In the above the `DEPENDENCY_NAME` is the String name of a dependency. Most applications will have many such dependencies and many such code points where any specific dependency is accessed. Since this is just a String type (maybe with a character length limit) an adopter might alternatively use an endpoint URL.

The simple query interface shown above is easy to understand. Adopters are free to integrate it into their applications any way that makes sense for their context. Consider the following Java class called `EmployeeService` which uses the Spring Framework, and Spring Data JPA to create a repository pattern over the application's database.

```java
package com.whatever;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Transactional
    public Employee getEmployeeById(int id) {
        return employeeRepository.findOne(id);
    }

    @Transactional
    public void saveEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    @Transactional
    public void updateEmployee(Employee employee) {
        employeeRepository.save(employee);       
    }

    @Transactional
    public void deleteEmployee(int id) {
        employeeRepository.delete(id);
    }

    @Transactional
    public List<Employee> getAllEmployees() {
       return employeeRepository.findAll();
    }
}
```

In this application the developer never handles the network connection to the database, or even higher-level database interactions directly. All of that is abstracted by the various frameworks. Rather than asking an adopter to peel apart those abstractions (which differ for every combination of frameworks) it would be more appropriate to instrument the code where they're using the top-most framework-provided abstraction. In this example an adopter might write a small helper method to evaluate the attack gates and then call that prior to each invocation of the dependency.

```java
package com.whatever;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gremlin.fflags.FailureFlags;
import com.gremlin.fflags.Gremlins;

@Component
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private FailureFlags gremlin;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Transactional
    public Employee getEmployeeById(int id) {
        ffGate();
        return employeeRepository.findOne(id);
    }

    @Transactional
    public void saveEmployee(Employee employee) {
        ffGate();
        employeeRepository.save(employee);
    }

    @Transactional
    public void updateEmployee(Employee employee) {
        ffGate();
        employeeRepository.save(employee);       
    }

    @Transactional
    public void deleteEmployee(int id) {
        ffGate();
        employeeRepository.delete(id);
    }

    @Transactional
    public List<Employee> getAllEmployees() {
        ffGate();
        return employeeRepository.findAll();
    }

    private void ffGate() {
        if(gremlin.isDependencyTestActive("database", Gremlins.Latency)) {
            Gremlins.Latency(ONE_SECOND);
        } else if(gremlin.isDependencyTestActive("database", Gremlins.Availability)) {
            throw new JDBCConnectionException("Database is unreachable.");
        }
    }
}
```

###Auto-Configuration

In addition to the gremlin infrastructure agent config file typically located in `/etc/gremlin/config.yaml`, you can also add additional config for failure flags in `~/.gremlin/config.yaml`. The failure flags framework will look in both of these locations to auto-configure the failure flags agent. 

Example of additional configuration in `~/.gremlin/config.yaml`
```
region: us-west-1
zone: us-west-1a
build: 2.3.0
version: 1.1.1
tags:
  app: demo
```