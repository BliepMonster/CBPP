package compilation.ir.instructions.cf;

import compilation.ir.instructions.Instruction;
import compilation.ir.instructions.InstructionVisitor;

import java.util.ArrayList;

public record FunctionDefinitionInstruction(String name, ArrayList<Instruction> instructions) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitFunctionDefinitionInstruction(this);
    }
}
