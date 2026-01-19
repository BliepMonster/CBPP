package statements;

import expressions.Expression;
import main.VariableType;

public class VarStatement extends Statement {
    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitVarStatement(this);
    }
    public final Expression var, assignment;
    public final String type;

    public VarStatement(Expression var, Expression assignment, String type) {
        this.var = var;
        this.assignment = assignment;
        this.type = type;
    }
}
