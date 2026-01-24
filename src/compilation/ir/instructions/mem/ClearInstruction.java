package compilation.ir.instructions.mem;

import compilation.ir.UniqueVariable;
import compilation.ir.instructions.Instruction;
import compilation.ir.instructions.InstructionVisitor;

public record ClearInstruction(UniqueVariable register) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitClearInstruction(this);
    }
}
