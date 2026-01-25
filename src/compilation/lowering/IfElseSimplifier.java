package compilation.lowering;

import compilation.ir.UniqueVariable;
import compilation.ir.instructions.*;
import compilation.ir.instructions.cf.*;
import compilation.ir.instructions.math.*;
import compilation.ir.instructions.math.num.*;
import compilation.ir.instructions.mem.*;
import main.BoolType;

import java.util.ArrayList;
import java.util.Collections;

class IfElseSimplifierException extends RuntimeException {
    public IfElseSimplifierException(String s) {
        super(s);
    }
}

public class IfElseSimplifier implements InstructionVisitor<ArrayList<Instruction>> {
    public ArrayList<Instruction> simplify(ArrayList<Instruction> code) {
        ArrayList<Instruction> out = new ArrayList<>();
        for (Instruction i : code) {
            out.addAll(i.accept(this));
        }
        return out;
    }
    public static int counter = 0;
    public ArrayList<Instruction> visitAllocateInstruction(AllocateInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    public ArrayList<Instruction> visitCallInstruction(CallInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitAddInstruction(AddInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitPrintstrInstruction(PrintstrInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitGtInstruction(GtInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitAndInstruction(AndInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitBoolInstruction(BoolInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitClearInstruction(ClearInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitCopyInstruction(CopyInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitDivInstruction(DivInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitEqInstruction(EqInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitExpInstruction(ExpInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitFreeInstruction(FreeInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitIfInstruction(IfInstruction instr) {
        ArrayList<Instruction> out = new ArrayList<>();
        int id = counter++;
        String elseName = "#else_"+id;
        UniqueVariable uv = new UniqueVariable("#else", id, BoolType.INSTANCE);
        if (instr.hasElseBranch()) {
            out.add(new AllocateInstruction(elseName, 1));
            out.add(new PutInstruction(uv, (byte) 1));
        }
        ArrayList<Instruction> thenBranch = new ArrayList<>();
        for (Instruction i : instr.thenBranch()) {
            thenBranch.addAll(i.accept(this));
        }
        if (instr.hasElseBranch())
            thenBranch.add(new ClearInstruction(uv));
        out.add(new SimpleIfInstruction(instr.condition(), thenBranch));
        if (!instr.hasElseBranch())
            return out;

        ArrayList<Instruction> elseBranch = new ArrayList<>();
        for (Instruction i : instr.elseBranch()) {
            elseBranch.addAll(i.accept(this));
        }
        out.add(new SimpleIfInstruction(uv, elseBranch));
        out.add(new FreeInstruction(elseName));
        return out;
    }

    @Override
    public ArrayList<Instruction> visitInvInstruction(InvInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitModInstruction(ModInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitMulInstruction(MulInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitNativeInstruction(NativeInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitNegInstruction(NegInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitOrInstruction(OrInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitPrintInstruction(PrintInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitPutInstruction(PutInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitSimpleIfInstruction(SimpleIfInstruction instr) {
        throw new IfElseSimplifierException("Instruction should not exist before this stage");
    }

    @Override
    public ArrayList<Instruction> visitSubInstruction(SubInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitWhileInstruction(WhileInstruction instr) {
        ArrayList<Instruction> out = new ArrayList<>();
        ArrayList<Instruction> body = new ArrayList<>();
        for (Instruction i : instr.instruction()) {
            body.addAll(i.accept(this));
        }
        out.add(new WhileInstruction(body, instr.var()));
        return out;
    }

    @Override
    public ArrayList<Instruction> visitXorInstruction(XorInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitFunctionDefinitionInstruction(FunctionDefinitionInstruction instr) {
        ArrayList<Instruction> out = new ArrayList<>();
        ArrayList<Instruction> body = new ArrayList<>();
        for (Instruction i : instr.instructions()) {
            body.addAll(i.accept(this));
        }
        out.add(new FunctionDefinitionInstruction(instr.name(), body));
        return out;
    }
}
