package compilation.ir.instructions;

import compilation.ir.UniqueVariable;

public record AddInstruction(UniqueVariable v1, UniqueVariable v2, UniqueVariable result) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitAddInstruction(this);
    }
}
