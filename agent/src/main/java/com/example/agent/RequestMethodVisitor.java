package com.example.agent;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class RequestMethodVisitor extends MethodVisitor {

    public RequestMethodVisitor(MethodVisitor next) {
        super(Opcodes.ASM9, next);
    }

    @Override
    public void visitCode() {
        super.visitCode();
        emitPrintln("[Agent] >>> Request started");
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
            emitPrintln("[Agent] <<< Request finished");
        }
        super.visitInsn(opcode);
    }

    private void emitPrintln(String message) {
        mv.visitFieldInsn(Opcodes.GETSTATIC,
                "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn(message);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}
