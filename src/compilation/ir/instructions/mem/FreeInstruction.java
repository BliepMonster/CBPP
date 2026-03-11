package compilation.ir.instructions.mem;

import compilation.ir.UniqueVariable;
import compilation.ir.instructions.Instruction;
import compilation.ir.instructions.InstructionVisitor;

public record FreeInstruction(UniqueVariable uv) implements Instruction {
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitFreeInstruction(this);
    }
}
