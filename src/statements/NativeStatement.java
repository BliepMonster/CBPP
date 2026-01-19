package statements;

public class NativeStatement extends Statement {
    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitNativeStatement(this);
    }
    public final String code;

    public NativeStatement(String code) {
        this.code = code;
    }
}
