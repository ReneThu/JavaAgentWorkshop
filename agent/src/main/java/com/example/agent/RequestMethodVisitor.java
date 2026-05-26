package com.example.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class RequestMethodVisitor extends MethodVisitor {

    private static final String LOGGER = "com/example/agent/injectable/RequestLogger";

    public RequestMethodVisitor(MethodVisitor next) {
        super(Opcodes.ASM9, next);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, LOGGER, "onEntry",
                "(Ljava/lang/Object;)V", false);
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, LOGGER, "onExit",
                    "(Ljava/lang/Object;)V", false);
        }
        super.visitInsn(opcode);
    }
}
