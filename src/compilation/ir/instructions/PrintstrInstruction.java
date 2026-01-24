package compilation.ir.instructions;

public record PrintstrInstruction(String str) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitPrintstrInstruction(this);
    }
}
