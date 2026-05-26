package com.example.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class MyAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("[Agent] Hello from premain! The agent is loaded.");

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                                    Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain,
                                    byte[] classfileBuffer) {
                if (className != null) {
                    System.out.println("[Agent] Loading: " + className);
                }
                if ("com/example/app/HelloController".equals(className)) {
                    System.out.println("[Agent] >>> Found HelloController!");
                }
                return null;
            }
        });
    }
}
