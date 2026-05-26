package com.example.agent;

import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class RequestClassTransformer implements ClassFileTransformer {

    private static final String TARGET = "org/springframework/web/servlet/FrameworkServlet";
    private static final String LOGGER_CLASS = "com.example.agent.injectable.RequestLogger";
    private static final String LOGGER_RESOURCE = "/injectable/com/example/agent/injectable/RequestLogger.class";

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        if (!TARGET.equals(className)) {
            return null;
        }
        System.out.println("[Agent] Instrumenting " + className);

        injectLoggerClass(loader);

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
        reader.accept(new RequestClassVisitor(writer), 0);
        return writer.toByteArray();
    }

    private static void injectLoggerClass(ClassLoader targetClassLoader) {
        try {
            byte[] classBytes;
            try (InputStream is = RequestClassTransformer.class.getResourceAsStream(LOGGER_RESOURCE)) {
                classBytes = is.readAllBytes();
            }

            Method defineClass = ClassLoader.class.getDeclaredMethod(
                "defineClass", String.class, byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            defineClass.invoke(targetClassLoader, LOGGER_CLASS, classBytes, 0, classBytes.length);

            System.out.println("[Agent] Injected RequestLogger into "
                + targetClassLoader.getClass().getSimpleName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject RequestLogger", e);
        }
    }
}
