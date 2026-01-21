package statements;

public interface StatementVisitor<R> {
    R visitExpressionStatement(ExpressionStatement stmt);
    R visitPrintStatement(PrintStatement stmt);
    R visitVarStatement(VarStatement stmt);
    R visitBlockStatement(BlockStatement stmt);
    R visitIfStatement(IfStatement stmt);
    R visitWhileStatement(WhileStatement stmt);
    R visitNativeStatement(NativeStatement stmt);
    R visitFunctionStatement(FunctionStatement stmt);
    R visitStructStatement(StructStatement stmt);
    R visitReturnStatement(ReturnStatement stmt);
}
