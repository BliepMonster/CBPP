package compilation.ir.instructions;

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
}
