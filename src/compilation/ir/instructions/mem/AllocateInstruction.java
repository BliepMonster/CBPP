package compilation.ir.instructions.mem;

import compilation.ir.instructions.Instruction;
import compilation.ir.instructions.InstructionVisitor;

public record AllocateInstruction(String name, int size) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitAllocateInstruction(this);
    }
}
