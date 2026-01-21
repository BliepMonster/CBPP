package compilation.ir.instructions;

public record FreeInstruction(String name) implements Instruction {
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitFreeInstruction(this);
    }
}
