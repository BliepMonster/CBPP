package statements;

public interface StatementVisitor<R> {
    R visitExpressionStatement(ExpressionStatement stmt);
    R visitPrintStatement(PrintStatement stmt);
}
