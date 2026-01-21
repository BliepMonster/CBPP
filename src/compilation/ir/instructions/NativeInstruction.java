package compilation.ir.instructions;

public record NativeInstruction(String str) implements Instruction {
    @Override
    public <R> R accept(InstructionVisitor<R> visitor) {
        return visitor.visitNativeInstruction(this);
    }
}
