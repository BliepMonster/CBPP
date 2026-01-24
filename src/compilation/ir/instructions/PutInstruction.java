package compilation.ir.instructions;

import compilation.ir.UniqueVariable;

public record PutInstruction(UniqueVariable variable, byte value) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitPutInstruction(this);
    }
}
