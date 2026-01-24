package compilation.ir.instructions.math;

import compilation.ir.UniqueVariable;
import compilation.ir.instructions.Instruction;
import compilation.ir.instructions.InstructionVisitor;

public record AndInstruction(UniqueVariable v1, UniqueVariable v2, UniqueVariable result) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitAndInstruction(this);
    }
}
