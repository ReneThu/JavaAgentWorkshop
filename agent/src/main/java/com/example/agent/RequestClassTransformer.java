package com.example.agent;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class RequestClassTransformer implements ClassFileTransformer {

    private static final String TARGET = "org/springframework/web/servlet/FrameworkServlet";

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        if (!TARGET.equals(className)) {
            return null;
        }
        System.out.println("[Agent] Instrumenting " + className);

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
        reader.accept(new RequestClassVisitor(writer), 0);
        return writer.toByteArray();
    }
}
