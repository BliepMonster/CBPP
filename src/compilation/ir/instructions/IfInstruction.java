package compilation.ir.instructions;

import compilation.ir.UniqueVariable;

import java.util.ArrayList;

public record IfInstruction(UniqueVariable condition, ArrayList<Instruction> thenBranch, ArrayList<Instruction> elseBranch) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitIfInstruction(this);
    }
    public boolean hasElseBranch() {
        return elseBranch != null;
    }
}
