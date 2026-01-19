package main;

import expressions.*;
import statements.*;

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

    @Override
    public String visitVarStatement(VarStatement stmt) {
        return "VAR: "+stmt.var.accept(this)+"("+stmt.type+")="+stmt.assignment.accept(this);
    }
    public String visitBlockStatement(BlockStatement stmt) {
        StringBuilder sb = new StringBuilder("BLOCK {\n\t");
        for (Statement s : stmt.statements) {
            sb.append("\n\t").append(s.accept(this));
        }
        return sb.append("\n}").toString();
    }
    public String visitIfStatement(IfStatement stmt) {
        return "IF ("+stmt.expr.accept(this)+"): "+stmt.thenBranch.accept(this)+(stmt.hasElseStatement() ? ", else: "+stmt.elseBranch.accept(this) : "");
    }
    public String visitWhileStatement(WhileStatement stmt) {
        return "WHILE ("+stmt.expr.accept(this)+"): "+stmt.stmt.accept(this);
    }
    public String visitCallExpression(CallExpression expr) {
        StringBuilder sb = new StringBuilder("CALL "+expr.function+"(");
        for (Expression expression : expr.args) {
            sb.append(expression.accept(this)+",");
        }
        return sb.append(")").toString();
    }
    public String visitNativeStatement(NativeStatement stmt) {
        return "NATIVE {"+stmt.code+"}";
    }
}
