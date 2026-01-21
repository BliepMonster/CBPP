package compilation.ir.instructions;

import compilation.ir.UniqueVariable;

public record PrintInstruction(UniqueVariable var) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitPrintInstruction(this);
    }
}
