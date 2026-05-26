package com.example.agent.injectable;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ServiceLogger {

    public static void onEntry(String methodName) {
        RequestContext ctx = currentContext();
        if (ctx == null) {
            System.out.printf("[Agent] -> %s (no request context)%n", methodName);
            return;
        }
        System.out.printf("[Agent] -> %s called by %s %s%n", methodName, ctx.method, ctx.uri);
    }

    public static void onExit(String methodName) {
        RequestContext ctx = currentContext();
        if (ctx == null) {
            System.out.printf("[Agent] <- %s%n", methodName);
            return;
        }
        long elapsedMicros = (System.nanoTime() - ctx.startNanos) / 1_000L;
        System.out.printf("[Agent] <- %s returned after %d us into %s %s%n",
                methodName, elapsedMicros, ctx.method, ctx.uri);
    }

    private static RequestContext currentContext() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return null;
            }
            HttpServletRequest req = attrs.getRequest();
            return RequestLogger.CONTEXTS.get(req);
        } catch (Throwable t) {
            return null;
        }
    }
}
