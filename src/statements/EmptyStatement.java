package statements;

public class EmptyStatement extends Statement {
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitEmptyStatement(this);
    }
}
