package com.example.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ServiceMethodVisitor extends MethodVisitor {

    private static final String LOGGER = "com/example/agent/injectable/ServiceLogger";

    private final String methodName;

    public ServiceMethodVisitor(MethodVisitor next, String methodName) {
        super(Opcodes.ASM9, next);
        this.methodName = methodName;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        mv.visitLdcInsn(methodName);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, LOGGER, "onEntry",
                "(Ljava/lang/String;)V", false);
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
            mv.visitLdcInsn(methodName);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, LOGGER, "onExit",
                    "(Ljava/lang/String;)V", false);
        }
        super.visitInsn(opcode);
    }
}
