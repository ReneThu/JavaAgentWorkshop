package com.example.agent.injectable;

public class RequestContext {

    public final String method;
    public final String uri;
    public final long startNanos;

    public RequestContext(String method, String uri, long startNanos) {
        this.method = method;
        this.uri = uri;
        this.startNanos = startNanos;
    }
}
