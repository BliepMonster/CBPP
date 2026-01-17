package expressions;

public class IdentifierExpression extends Expression {
    public final String name;
    public IdentifierExpression(String name) {
        this.name = name;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visitIdentifierExpression(this);
    }
}
