package com.example.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class RequestClassVisitor extends ClassVisitor {

    private final String className;

    public RequestClassVisitor(ClassVisitor next, String className) {
        super(Opcodes.ASM9, next);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        if ("org/springframework/web/servlet/FrameworkServlet".equals(className)
                && "service".equals(name)
                && descriptor.startsWith("(Ljakarta/servlet/http/HttpServletRequest;")) {
            return new RequestMethodVisitor(mv);
        }
        if ("com/example/app/GreetingService".equals(className) && "greet".equals(name)) {
            return new ServiceMethodVisitor(mv, name);
        }
        return mv;
    }
}

