package compilation.ir.instructions;

import compilation.ir.UniqueVariable;

import java.util.ArrayList;

public record WhileInstruction(ArrayList<Instruction> instruction, UniqueVariable var) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitWhileInstruction(this);
    }
}
