package compilation.ir.instructions.math;

import compilation.ir.UniqueVariable;
import compilation.ir.instructions.Instruction;
import compilation.ir.instructions.InstructionVisitor;

public record InvInstruction(UniqueVariable v, UniqueVariable result) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitInvInstruction(this);
    }
}
