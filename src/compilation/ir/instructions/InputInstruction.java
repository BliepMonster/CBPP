package compilation.ir.instructions;

import compilation.ir.UniqueVariable;

public record InputInstruction(UniqueVariable uv) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitInputInstruction(this);
    }
}
