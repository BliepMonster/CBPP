package expressions;

public class InputExpression extends Expression {
    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitInputExpression(this);
    }
}
