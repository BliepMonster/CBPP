package statements;

import expressions.Expression;

public class PrintStatement extends Statement {
    public final Expression expr;
    public PrintStatement(Expression expr) {
        this.expr = expr;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitPrintStatement(this);
    }
}
