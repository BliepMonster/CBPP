package compilation.ir.instructions;

public record AllocateInstruction(String name, int size) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitAllocateInstruction(this);
    }
}
