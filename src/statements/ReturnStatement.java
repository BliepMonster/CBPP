package statements;

import expressions.Expression;

public class ReturnStatement extends Statement {
    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitReturnStatement(this);
    }
    public final Expression expr;

    public ReturnStatement(Expression expr) {
        this.expr = expr;
    }
}
