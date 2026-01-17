package expressions;

public interface ExpressionVisitor<R> {
    R visitBinaryExpression(BinaryExpression expr);
    R visitUnaryExpression(UnaryExpression expr);
    R visitLiteralExpression(LiteralExpression expr);
    R visitIdentifierExpression(IdentifierExpression expr);
}
