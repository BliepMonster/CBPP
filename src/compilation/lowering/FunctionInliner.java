package compilation.lowering;

import compilation.ir.instructions.*;
import compilation.ir.instructions.cf.*;
import compilation.ir.instructions.math.*;
import compilation.ir.instructions.math.num.*;
import compilation.ir.instructions.mem.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

class FunctionInlinerException extends RuntimeException {
    public FunctionInlinerException(String s) {
        super(s);
    }
}

public class FunctionInliner implements InstructionVisitor<ArrayList<Instruction>> {
    HashMap<String, ArrayList<Instruction>> macros = new HashMap<>();
    public ArrayList<Instruction> inline(ArrayList<Instruction> code) {
        ArrayList<Instruction> out = new ArrayList<>();
        for (Instruction i : code) {
            out.addAll(i.accept(this));
        }
        return out;
    }
    @Override
    public ArrayList<Instruction> visitPrintstrInstruction(PrintstrInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitAddInstruction(AddInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitCallInstruction(CallInstruction instr) {
        return macros.get(instr.fname());
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
    public ArrayList<Instruction> visitClearInstruction(ClearInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitCopyInstruction(CopyInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitFreeInstruction(FreeInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitBoolInstruction(BoolInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitDivInstruction(DivInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitIfInstruction(IfInstruction instr) {
        throw new FunctionInlinerException("Statement should not exist");
    }

    @Override
    public ArrayList<Instruction> visitEqInstruction(EqInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitModInstruction(ModInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitExpInstruction(ExpInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitInvInstruction(InvInstruction instr) {
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
    public ArrayList<Instruction> visitSimpleIfInstruction(SimpleIfInstruction instr) {
        throw new FunctionInlinerException("Instruction should not exist");
    }

    @Override
    public ArrayList<Instruction> visitPrintInstruction(PrintInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitWhileInstruction(WhileInstruction instr) {
        ArrayList<Instruction> body = new ArrayList<>();
        for (Instruction i : instr.instruction()) {
            body.addAll(i.accept(this));
        }
        return new ArrayList<>(Collections.singleton(new WhileInstruction(body, instr.var())));
    }

    @Override
    public ArrayList<Instruction> visitFunctionDefinitionInstruction(FunctionDefinitionInstruction instr) {
        ArrayList<Instruction> body = new ArrayList<>();
        for (Instruction i : instr.instructions())
            body.addAll(i.accept(this));
        macros.put(instr.name(), body);
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Instruction> visitAllocateInstruction(AllocateInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitOrInstruction(OrInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitSubInstruction(SubInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitXorInstruction(XorInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitPutInstruction(PutInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitInputInstruction(InputInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }
    public ArrayList<Instruction> visitBitwiseOrInstruction(BitwiseOrInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitBitwiseAndInstruction(BitwiseAndInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }

    @Override
    public ArrayList<Instruction> visitBitwiseXorInstruction(BitwiseXorInstruction instr) {
        return new ArrayList<>(Collections.singleton(instr));
    }
}
