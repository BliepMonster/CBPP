package compilation.ir.instructions;

import compilation.ir.UniqueVariable;

public record CopyInstruction(UniqueVariable var1, UniqueVariable var2) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitCopyInstruction(this);
    }
}
