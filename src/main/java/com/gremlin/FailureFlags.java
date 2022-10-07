package com.gremlin;

public interface FailureFlags {
   boolean isDependencyTestActive(String dependencyName, TestType test);
}
