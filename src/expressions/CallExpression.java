package expressions;

import java.util.ArrayList;

public class CallExpression extends Expression {
    public final String function;
    public final ArrayList<Expression> args;

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitCallExpression(this);
    }

    public CallExpression(String function, ArrayList<Expression> args) {
        this.function = function;
        this.args = args;
    }
}
