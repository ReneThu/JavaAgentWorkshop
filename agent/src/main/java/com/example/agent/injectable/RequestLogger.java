package com.example.agent.injectable;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import jakarta.servlet.http.HttpServletRequest;

public class RequestLogger {

    public static final Map<HttpServletRequest, RequestContext> CONTEXTS =
            Collections.synchronizedMap(new WeakHashMap<>());

    public static void onEntry(Object request) {
        HttpServletRequest req = (HttpServletRequest) request;
        RequestContext ctx = new RequestContext(req.getMethod(), req.getRequestURI(), System.nanoTime());
        CONTEXTS.put(req, ctx);
        System.out.printf("[Agent] >>> %s %s%n", ctx.method, ctx.uri);
    }

    public static void onExit(Object request) {
        HttpServletRequest req = (HttpServletRequest) request;
        RequestContext ctx = CONTEXTS.remove(req);
        if (ctx == null) {
            System.out.printf("[Agent] <<< %s %s completed%n", req.getMethod(), req.getRequestURI());
            return;
        }
        long elapsedMicros = (System.nanoTime() - ctx.startNanos) / 1_000L;
        System.out.printf("[Agent] <<< %s %s completed in %d us%n", ctx.method, ctx.uri, elapsedMicros);
    }
}

