package optimization;

import expressions.*;
import statements.*;

import java.util.ArrayList;
import java.util.List;

class ConstantFolderException extends RuntimeException {
    public ConstantFolderException(String s) {
        super(s);
    }
}

public class ConstantFolder implements ExpressionVisitor<Expression>,  StatementVisitor<Statement> {
    public ArrayList<Statement> optimize(List<Statement> stmts) {
        ArrayList<Statement> list = new ArrayList<>();
        for (Statement stmt : stmts)
            list.add(stmt.accept(this));
        return list;
    }
    public Expression visitLiteralExpression(LiteralExpression expr) {
        return expr;
    }
    public Expression visitDotExpression(DotExpression expr) {
        return expr;
    }
    public Expression visitCallExpression(CallExpression expr) {
        ArrayList<Expression> args = new ArrayList<>();
        for (Expression e : expr.args)
            args.add(e.accept(this));
        return new CallExpression(expr.function, args);
    }
    public Expression visitIdentifierExpression(IdentifierExpression expr) {
        return expr;
    }
    public Expression visitInputExpression(InputExpression expr) {
        return expr;
    }
    public Expression visitUnaryExpression(UnaryExpression expr) {
        Expression e = expr.expr.accept(this);
        if (!(e instanceof LiteralExpression lit))
            return new UnaryExpression(e, expr.operator);

        switch (expr.operator.type) {
            case MINUS -> {
                Object o = lit.value;
                if (!(o instanceof Integer i))
                    throw new ConstantFolderException("Invalid operand");
                return new LiteralExpression(-i);
            }
            case QUESTION -> {
                Object o = lit.value;
                if (!(o instanceof Integer i))
                    throw new ConstantFolderException("Invalid operand");
                return new LiteralExpression(i != 0);
            }
            case BANG -> {
                Object o = lit.value;
                if (!(o instanceof Boolean b))
                    throw new ConstantFolderException("Invalid operand");
                return new LiteralExpression(!b);
            }
            default -> throw new ConstantFolderException("Invalid operation");
        }
    }
    public Expression visitBinaryExpression(BinaryExpression expr) {
        Expression left1 = expr.left.accept(this);
        Expression right1 = expr.right.accept(this);
        if (!(left1 instanceof LiteralExpression left))
            return new BinaryExpression(left1, expr.operator, right1);
        if (!(right1 instanceof LiteralExpression right))
            return new BinaryExpression(left1, expr.operator, right1);
        switch (expr.operator.type) {
            case PLUS -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l+r);
            }
            case MINUS -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l-r);
            }
            case STAR -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l*r);
            }
            case SLASH -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l/r);
            }
            case MOD -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l%r);
            }
            case BITAND -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l&r);
            }
            case BITOR -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l|r);
            }
            case BITXOR -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l^r);
            }
            case EXPONENT -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression((int) Math.pow(l, r));
            }
            case EQEQ -> {
                return new LiteralExpression(left.value.equals(right.value));
            }
            case NEQ -> {
                return new LiteralExpression(!left.value.equals(right.value));
            }
            case GT -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l > r);
            }
            case LT -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l < r);
            }
            case GTEQ -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l >= r);
            }
            case LTEQ -> {
                if (!(left.value instanceof Integer l) || !(right.value instanceof Integer r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l <= r);
            }
            case OR -> {
                if (!(left.value instanceof Boolean l) || !(right.value instanceof Boolean r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l || r);
            }
            case AND -> {
                if (!(left.value instanceof Boolean l) || !(right.value instanceof Boolean r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l && r);
            }
            case XOR -> {
                if (!(left.value instanceof Boolean l) || !(right.value instanceof Boolean r))
                    throw new ConstantFolderException("Invalid operands");
                return new LiteralExpression(l != r);
            }
            default -> throw new ConstantFolderException("Invalid operation");
        }
    }

    @Override
    public Statement visitReturnStatement(ReturnStatement stmt) {
        return new ReturnStatement(stmt.expr.accept(this));
    }

    @Override
    public Statement visitVarStatement(VarStatement stmt) {
        return new VarStatement(stmt.var, stmt.assignment.accept(this), stmt.type);
    }

    @Override
    public Statement visitStructStatement(StructStatement stmt) {
        return stmt;
    }

    @Override
    public Statement visitPrintStatement(PrintStatement stmt) {
        return new PrintStatement(stmt.expr.accept(this));
    }

    @Override
    public Statement visitBlockStatement(BlockStatement stmt) {
        ArrayList<Statement> stmts = new ArrayList<>();
        for (Statement st : stmt.statements) {
            stmts.add(st.accept(this));
        }
        return new BlockStatement(stmts);
    }

    @Override
    public Statement visitExpressionStatement(ExpressionStatement stmt) {
        return new ExpressionStatement(stmt.expr.accept(this));
    }

    @Override
    public Statement visitFunctionStatement(FunctionStatement stmt) {
        ArrayList<Statement> stmts = new ArrayList<>();
        for (Statement st : stmt.body) {
            stmts.add(st.accept(this));
        }
        return new FunctionStatement(stmt.name, stmt.args, stmts, stmt.returnType);
    }

    @Override
    public Statement visitIfStatement(IfStatement stmt) {
        if (!stmt.hasElseStatement())
            return new IfStatement(stmt.expr.accept(this), stmt.thenBranch.accept(this), null);
        return new IfStatement(stmt.expr.accept(this), stmt.thenBranch.accept(this), stmt.elseBranch.accept(this));
    }

    @Override
    public Statement visitPrintstrStatement(PrintstrStatement stmt) {
        return stmt;
    }

    @Override
    public Statement visitNativeStatement(NativeStatement stmt) {
        return stmt;
    }

    @Override
    public Statement visitWhileStatement(WhileStatement stmt) {
        return new WhileStatement(stmt.expr.accept(this), stmt.stmt.accept(this));
    }
}
