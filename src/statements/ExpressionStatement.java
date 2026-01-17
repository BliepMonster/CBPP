package statements;

import expressions.Expression;

public class ExpressionStatement extends Statement {
    public final Expression expr;
    public ExpressionStatement(Expression expr) {
        this.expr = expr;
    }
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitExpressionStatement(this);
    }
}
