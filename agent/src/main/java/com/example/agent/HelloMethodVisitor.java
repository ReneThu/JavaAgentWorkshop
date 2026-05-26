package com.example.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HelloMethodVisitor extends MethodVisitor {

    public HelloMethodVisitor(MethodVisitor next) {
        super(Opcodes.ASM9, next);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("[Agent] HelloController.hello() called!");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}
