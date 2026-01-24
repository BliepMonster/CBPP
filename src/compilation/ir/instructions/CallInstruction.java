package compilation.ir.instructions;

public record CallInstruction(String fname) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitCallInstruction(this);
    }
}
