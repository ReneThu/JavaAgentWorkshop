package com.example.agent;

import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class RequestClassTransformer implements ClassFileTransformer {

    private static final String FRAMEWORK_TARGET = "org/springframework/web/servlet/FrameworkServlet";
    private static final String SERVICE_TARGET = "com/example/app/GreetingService";

    private static final String[] INJECTABLES = {
        "com.example.agent.injectable.RequestContext",
        "com.example.agent.injectable.RequestLogger",
        "com.example.agent.injectable.ServiceLogger"
    };

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        if (!FRAMEWORK_TARGET.equals(className) && !SERVICE_TARGET.equals(className)) {
            return null;
        }
        System.out.println("[Agent] Instrumenting " + className);

        for (String injectable : INJECTABLES) {
            injectClass(loader, injectable);
        }

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
        reader.accept(new RequestClassVisitor(writer, className), 0);
        return writer.toByteArray();
    }

    private static void injectClass(ClassLoader targetClassLoader, String fqcn) {
        String resource = "/injectable/" + fqcn.replace('.', '/') + ".class";
        try {
            byte[] classBytes;
            try (InputStream is = RequestClassTransformer.class.getResourceAsStream(resource)) {
                if (is == null) {
                    throw new IllegalStateException("Missing resource: " + resource);
                }
                classBytes = is.readAllBytes();
            }

            Method defineClass = ClassLoader.class.getDeclaredMethod(
                "defineClass", String.class, byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            defineClass.invoke(targetClassLoader, fqcn, classBytes, 0, classBytes.length);

            System.out.println("[Agent] Injected " + fqcn + " into "
                + targetClassLoader.getClass().getSimpleName());
        } catch (java.lang.reflect.InvocationTargetException ite) {
            if (ite.getCause() instanceof LinkageError) {
                return;
            }
            throw new RuntimeException("Failed to inject " + fqcn, ite);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject " + fqcn, e);
        }
    }
}

