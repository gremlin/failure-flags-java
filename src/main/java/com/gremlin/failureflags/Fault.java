package com.gremlin.failureflags;


import com.gremlin.failureflags.interfaces.Behavior;


import java.util.Map;

public class Fault implements Behavior {

    public static class ExceptionObject extends Throwable {
    }

    public static class DataObject {


    }

}