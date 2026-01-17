package expressions;

import main.Token;

public class UnaryExpression extends Expression {
    public final Expression expr;
    public final Token operator;

    public UnaryExpression(Expression expr, Token operator) {
        this.expr = expr;
        this.operator = operator;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitUnaryExpression(this);
    }
}
