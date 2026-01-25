package compilation.ir.instructions.cf;

import compilation.ir.instructions.Instruction;
import compilation.ir.instructions.InstructionVisitor;

public record CallInstruction(String fname) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitCallInstruction(this);
    }
}
