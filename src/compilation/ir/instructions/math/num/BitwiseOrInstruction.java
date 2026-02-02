package compilation.ir.instructions.math.num;

import compilation.ir.UniqueVariable;
import compilation.ir.instructions.Instruction;
import compilation.ir.instructions.InstructionVisitor;

public record BitwiseOrInstruction(UniqueVariable v1, UniqueVariable v2, UniqueVariable result) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitBitwiseOrInstruction(this);
    }
}
