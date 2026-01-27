package compilation.bpp;

import static compilation.bpp.IrCompiler.*;

public final class InstructionCreator {
    private InstructionCreator() {}
    public static String buildPutInstruction(int value, SimpleRegister reg) {
        return "put "+value+", "+reg+";";
    }
    public static String buildClearInstruction(SimpleRegister reg) {
        return "clear "+reg+";";
    }
    public static String buildIncInstruction(SimpleRegister reg, int value) {
        return "inc "+reg+", "+value+";";
    }
    public static String buildDecInstruction(SimpleRegister reg, int value) {
        return "dec "+reg+", "+value+";";
    }
    public static String buildAddInstruction(SimpleRegister r1, SimpleRegister r2, SimpleRegister r3) {
        return "add "+r1+", "+r2+", "+r3+";";
    }
    public static String buildSubInstruction(SimpleRegister r1, SimpleRegister r2, SimpleRegister r3) {
        return "sub "+r1+", "+r2+", "+r3+";";
    }
    public static String buildMulInstruction(SimpleRegister r1, SimpleRegister r2, SimpleRegister r3) {
        return "mul "+r1+", "+r2+", "+r3+";";
    }
    public static String buildCopyInstruction(SimpleRegister r1, SimpleRegister r2) {
        return "copy "+r1+", "+r2+";";
    }
    public static String buildMoveInstruction(SimpleRegister r1, SimpleRegister r2) {
        return "move "+r1+", "+r2+";";
    }
    public static String buildSwapInstruction(SimpleRegister r1, SimpleRegister r2) {
        return "swap "+r1+", "+r2+";";
    }
    public static String buildWhileInstruction(SimpleRegister reg, String body) {
        return "while "+reg+" {"+body+"};";
    }
    public static String buildPrintInstruction(SimpleRegister reg) {
        return "print "+reg+";";
    }
    public static String buildInputInstruction(SimpleRegister reg) {
        return "input "+reg+";";
    }
    public static String buildPrintstrInstruction(String str) {
        return "printstr \""+str+"\";";
    }
    public static String buildInvInstruction(SimpleRegister r1, SimpleRegister r2) {
        return "inv "+r1+", "+r2+";";
    }
    public static String buildDivModInstruction(SimpleRegister r1, SimpleRegister r2, SimpleRegister r3, SimpleRegister r4) {
        return "divmod "+r1+", "+r2+", "+r3+", "+r4+";";
    }
    public static String buildEqInstruction(SimpleRegister r1, SimpleRegister r2, SimpleRegister r3) {
        return "eq "+r1+", "+r2+", "+r3+";";
    }
    public static String buildAndInstruction(SimpleRegister r1, SimpleRegister r2, SimpleRegister r3) {
        return buildCopyInstruction(r1, new SimpleRegister(B1)) +
                buildCopyInstruction(r2, new SimpleRegister(B2)) +
                "and;" +
                buildCopyInstruction(new SimpleRegister(BR), r3);
    }
    public static String buildOrInstruction(SimpleRegister r1, SimpleRegister r2, SimpleRegister r3) {
        return buildCopyInstruction(r1, new SimpleRegister(B1)) +
                buildCopyInstruction(r2, new SimpleRegister(B2)) +
                "or;" +
                buildCopyInstruction(new SimpleRegister(BR), r3);
    }
    public static String buildXorInstruction(SimpleRegister r1, SimpleRegister r2, SimpleRegister r3) {
        return buildCopyInstruction(r1, new SimpleRegister(B1)) +
                buildCopyInstruction(r2, new SimpleRegister(B2)) +
                "xor;" +
                buildCopyInstruction(new SimpleRegister(BR), r3);
    }
    public static String buildGtInstruction(SimpleRegister r1, SimpleRegister r2, SimpleRegister r3) {
        return buildCopyInstruction(r1, new SimpleRegister(COMP1)) +
                buildCopyInstruction(r2, new SimpleRegister(COMP2)) +
                "gt;" +
                buildCopyInstruction(new SimpleRegister(COMPR), r3);
    }
    public static String buildBoolInstruction(SimpleRegister r1, SimpleRegister r2) {
        return buildCopyInstruction(r1, new SimpleRegister(BOOL_IN)) +
                "bool;" +
                buildCopyInstruction(new SimpleRegister(BOOL_OUT), r2);
    }
}
