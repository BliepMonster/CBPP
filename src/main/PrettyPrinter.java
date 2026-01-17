package main;

import expressions.*;
import statements.ExpressionStatement;
import statements.PrintStatement;
import statements.Statement;
import statements.StatementVisitor;

import java.util.List;

public class PrettyPrinter implements ExpressionVisitor<String>, StatementVisitor<String> {
    public void print(List<Statement> stmts) {
        for (Statement stmt : stmts) {
            System.out.println(stmt.accept(this));
        }
    }

    public String visitExpressionStatement(ExpressionStatement stmt) {
        return "EXPR: "+stmt.expr.accept(this);
    }

    @Override
    public String visitPrintStatement(PrintStatement stmt) {
        return "PRINT: "+stmt.expr.accept(this);
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
