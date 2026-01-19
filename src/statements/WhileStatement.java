package statements;

import expressions.Expression;

public class WhileStatement extends Statement {
    public final Expression expr;
    public final Statement stmt;
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitWhileStatement(this);
    }

    public WhileStatement(Expression expr, Statement stmt) {
        this.expr = expr;
        this.stmt = stmt;
    }
}
