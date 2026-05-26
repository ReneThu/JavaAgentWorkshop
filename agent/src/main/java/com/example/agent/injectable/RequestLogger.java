package com.example.agent.injectable;

public class RequestLogger {

    public static void onEntry(Object request) {
        System.out.println("[Agent] >>> Request started");
    }

    public static void onExit(Object request) {
        System.out.println("[Agent] <<< Request finished");
    }
}
