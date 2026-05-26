package com.example.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class RequestClassVisitor extends ClassVisitor {

    public RequestClassVisitor(ClassVisitor next) {
        super(Opcodes.ASM9, next);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        if ("service".equals(name) && descriptor.startsWith("(Ljakarta/servlet/http/HttpServletRequest;")) {
            return new RequestMethodVisitor(mv);
        }
        return mv;
    }
}
