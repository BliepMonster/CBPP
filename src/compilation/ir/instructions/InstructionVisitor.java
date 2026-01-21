package compilation.ir.instructions;

public interface InstructionVisitor<R> {
    R visitAllocateInstruction(AllocateInstruction instr);
    R visitFreeInstruction(FreeInstruction instr);
}
