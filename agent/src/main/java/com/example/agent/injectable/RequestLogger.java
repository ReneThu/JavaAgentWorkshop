package com.example.agent.injectable;

import jakarta.servlet.http.HttpServletRequest;

public class RequestLogger {

    public static void onEntry(Object request) {
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.printf("[Agent] >>> %s %s%n", req.getMethod(), req.getRequestURI());
    }

    public static void onExit(Object request) {
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.printf("[Agent] <<< %s %s completed%n", req.getMethod(), req.getRequestURI());
    }
}
