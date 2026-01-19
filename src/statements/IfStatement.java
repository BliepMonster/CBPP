package statements;

import expressions.Expression;

public class IfStatement extends Statement {
    public boolean hasElseStatement() {
        return elseBranch != null;
    }
    public final Expression expr;
    public final Statement thenBranch, elseBranch;

    public IfStatement(Expression expr, Statement thenBranch, Statement elseBranch) {
        this.expr = expr;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitIfStatement(this);
    }
}
