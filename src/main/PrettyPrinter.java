package main;

import expressions.*;
public class PrettyPrinter implements ExpressionVisitor<String> {
    public void print(Expression e) {
        System.out.println(e.accept(this));
    }
    public String visitBinaryExpression(BinaryExpression expr) {
        String l = expr.left.accept(this);
        String r = expr.right.accept(this);
        return "("+l+expr.operator.text+r+")";
    }
    public String visitUnaryExpression(UnaryExpression expr) {
        return "("+expr.operator.text+expr.expr.accept(this)+")";
    }
    public String visitLiteralExpression(LiteralExpression expr) {
        return expr.value.toString();
    }
    public String visitIdentifierExpression(IdentifierExpression expr) {
        return expr.name;
    }
}
