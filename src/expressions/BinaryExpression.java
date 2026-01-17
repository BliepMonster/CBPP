package expressions;

import main.Token;

public class BinaryExpression extends Expression {
    public BinaryExpression(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    public final Expression left;
    public final Token operator;
    public final Expression right;
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitBinaryExpression(this);
    }
}
