package compilation.lowering;

import compilation.ir.instructions.Instruction;

import java.lang.reflect.Array;
import java.util.ArrayList;

public final class Lowerer {
    private Lowerer() {}
    public static ArrayList<Instruction> lower(ArrayList<Instruction> code) {
        return new FunctionInliner().inline(new IfWhileSimplifier().simplify(new IfElseSimplifier().simplify(code)));
    }
}
