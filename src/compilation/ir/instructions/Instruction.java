package compilation.ir.instructions;

public interface Instruction {
    <R> R accept(InstructionVisitor<R> visitor);
}
