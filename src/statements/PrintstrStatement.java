package statements;

public class PrintstrStatement extends Statement {
    public final String str;
    public PrintstrStatement(String str) {
        this.str = str;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitPrintstrStatement(this);
    }
}
