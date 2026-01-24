package compilation.ir.instructions.mem;

import compilation.ir.instructions.Instruction;
import compilation.ir.instructions.InstructionVisitor;

public record FreeInstruction(String name) implements Instruction {
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitFreeInstruction(this);
    }
}
