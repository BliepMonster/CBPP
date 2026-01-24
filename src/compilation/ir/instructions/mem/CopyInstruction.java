package compilation.ir.instructions.mem;

import compilation.ir.UniqueVariable;
import compilation.ir.instructions.Instruction;
import compilation.ir.instructions.InstructionVisitor;

public record CopyInstruction(UniqueVariable var1, UniqueVariable var2) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitCopyInstruction(this);
    }
}
