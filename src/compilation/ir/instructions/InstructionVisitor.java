package compilation.ir.instructions;

import compilation.ir.instructions.cf.*;
import compilation.ir.instructions.math.*;
import compilation.ir.instructions.math.num.*;
import compilation.ir.instructions.mem.*;

public interface InstructionVisitor<R> {
    R visitAllocateInstruction(AllocateInstruction instr);
    R visitFreeInstruction(FreeInstruction instr);
    R visitAddInstruction(AddInstruction instr);
    R visitPrintInstruction(PrintInstruction instr);
    R visitCopyInstruction(CopyInstruction instr);
    R visitWhileInstruction(WhileInstruction instr);
    R visitIfInstruction(IfInstruction instr);
    R visitClearInstruction(ClearInstruction instr);
    R visitNativeInstruction(NativeInstruction instr);
    R visitFunctionDefinitionInstruction(FunctionDefinitionInstruction instr);
    R visitSubInstruction(SubInstruction instr);
    R visitMulInstruction(MulInstruction instr);
    R visitDivInstruction(DivInstruction instr);
    R visitModInstruction(ModInstruction instr);
    R visitExpInstruction(ExpInstruction instr);
    R visitEqInstruction(EqInstruction instr);
    R visitInvInstruction(InvInstruction instr);
    R visitAndInstruction(AndInstruction instr);
    R visitOrInstruction(OrInstruction instr);
    R visitXorInstruction(XorInstruction instr);
    R visitGtInstruction(GtInstruction instr);
    R visitNegInstruction(NegInstruction instr);
    R visitBoolInstruction(BoolInstruction instr);
    R visitPrintstrInstruction(PrintstrInstruction instr);
    R visitPutInstruction(PutInstruction instr);
    R visitCallInstruction(CallInstruction instr);
    R visitSimpleIfInstruction(SimpleIfInstruction instr);
}
