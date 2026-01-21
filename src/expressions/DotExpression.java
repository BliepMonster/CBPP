package expressions;

public class DotExpression extends Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitDotExpression(this);
    }
    public final Expression left;
    public final String right;

    public DotExpression(Expression left, String right) {
        this.left = left;
        this.right = right;
    }
}
