package compilation;

import compilation.ir.instructions.*;
import compilation.ir.instructions.cf.*;
import compilation.ir.instructions.math.*;
import compilation.ir.instructions.math.num.*;
import compilation.ir.instructions.mem.*;

import java.util.List;

public class PrettyPrinter implements InstructionVisitor<String> {
    public void print(List<Instruction> instructions) {
        for (Instruction instr : instructions) {
            System.out.println(instr.accept(this));
        }
    }
    public String visitAllocateInstruction(AllocateInstruction instr) {
        return "alloc "+instr.name()+", "+instr.size()+";";
    }
    public String visitFreeInstruction(FreeInstruction instr) {
        return "free "+instr.name()+";";
    }
    public String visitAddInstruction(AddInstruction instr) {
        return "add "+instr.v1().getUniqueName()+", "+instr.v2().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitSubInstruction(SubInstruction instr) {
        return "sub "+instr.v1().getUniqueName()+", "+instr.v2().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitMulInstruction(MulInstruction instr) {
        return "mul "+instr.v1().getUniqueName()+", "+instr.v2().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitDivInstruction(DivInstruction instr) {
        return "div "+instr.v1().getUniqueName()+", "+instr.v2().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitPrintInstruction(PrintInstruction instr) {
        return "print "+instr.var().getUniqueName()+";";
    }
    public String visitCopyInstruction(CopyInstruction instr) {
        return "copy "+instr.var1().getUniqueName()+", "+instr.var2().getUniqueName()+";";
    }
    public String visitWhileInstruction(WhileInstruction instr) {
        StringBuilder sb = new StringBuilder("while "+instr.var().getUniqueName()+" {");
        for (Instruction i : instr.instruction()) {
            sb.append("\n\t")
                    .append(i.accept(this));
        }
        return sb.append("\n}").toString();
    }
    public String visitIfInstruction(IfInstruction instr) {
        StringBuilder sb = new StringBuilder("if "+instr.condition().getUniqueName()+" {");
        for (Instruction i : instr.thenBranch()) {
            sb.append("\n\t")
                    .append(i.accept(this));
        }
        sb.append("\n\t} else {");
        for (Instruction i : instr.elseBranch()) {
            sb.append("\n\t")
                    .append(i.accept(this));
        }
        return sb.append("\n}").toString();
    }
    public String visitAndInstruction(AndInstruction instr) {
        return "and "+instr.v1().getUniqueName()+", "+instr.v2().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitOrInstruction(OrInstruction instr) {
        return "or "+instr.v1().getUniqueName()+", "+instr.v2().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitXorInstruction(XorInstruction instr) {
        return "xor "+instr.v1().getUniqueName()+", "+instr.v2().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitClearInstruction(ClearInstruction instr) {
        return "clear "+instr.register().getUniqueName()+";";
    }
    public String visitNativeInstruction(NativeInstruction instr) {
        return "native \""+instr.str()+"\";";
    }
    public String visitFunctionDefinitionInstruction(FunctionDefinitionInstruction instr) {
        StringBuilder sb = new StringBuilder("fn "+instr.name()+" {");
        for (Instruction i : instr.instructions()) {
            sb.append("\n\t").append(i.accept(this));
        }
        return sb.append("\n}").toString();
    }
    public String visitModInstruction(ModInstruction instr) {
        return "mod "+instr.v1().getUniqueName()+", "+instr.v2().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitExpInstruction(ExpInstruction instr) {
        return "exp "+instr.v1().getUniqueName()+", "+instr.v2().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitEqInstruction(EqInstruction instr) {
        return "eq "+instr.v1().getUniqueName()+", "+instr.v2().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitInvInstruction(InvInstruction instr) {
        return "inv "+instr.v().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitGtInstruction(GtInstruction instr) {
        return "gt "+instr.v1().getUniqueName()+", "+instr.v2().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitNegInstruction(NegInstruction instr) {
        return "neg "+instr.v().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitBoolInstruction(BoolInstruction instr) {
        return "bool "+instr.v().getUniqueName()+", "+instr.result().getUniqueName()+";";
    }
    public String visitPrintstrInstruction(PrintstrInstruction instr) {
        return "printstr \""+instr.str()+"\";";
    }
    public String visitPutInstruction(PutInstruction instr) {
        return "put "+ instr.value() +", "+instr.variable().getUniqueName()+";";
    }
    public String visitCallInstruction(CallInstruction instr) {
        return "call "+instr.fname()+";";
    }
    public String visitSimpleIfInstruction(SimpleIfInstruction instr) {
        StringBuilder sb = new StringBuilder("simple if "+instr.condition().getUniqueName()+" {");
        for (Instruction i : instr.instructions()) {
            sb.append("\n\t")
                    .append(i.accept(this));
        }
        return sb.append("\n}").toString();
    }
}
