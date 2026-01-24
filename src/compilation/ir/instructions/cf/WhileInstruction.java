package compilation.ir.instructions.cf;

import compilation.ir.UniqueVariable;
import compilation.ir.instructions.Instruction;
import compilation.ir.instructions.InstructionVisitor;

import java.util.ArrayList;

public record WhileInstruction(ArrayList<Instruction> instruction, UniqueVariable var) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitWhileInstruction(this);
    }
}
