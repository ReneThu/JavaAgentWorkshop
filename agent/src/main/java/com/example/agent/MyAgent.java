package com.example.agent;

import java.lang.instrument.Instrumentation;

public class MyAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[Agent] Hello from premain! The agent is loaded.");
    }
}
