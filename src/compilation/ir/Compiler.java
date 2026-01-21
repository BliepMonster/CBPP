package compilation.ir;

import expressions.*;
import statements.*;

public class Compiler implements StatementVisitor<Void>, ExpressionVisitor<ExpressionResult> {
    private StringBuilder sb = new StringBuilder();
    private Scope scope = new Scope();
    public Void visitExpressionStatement(ExpressionStatement stmt) {

    }
    private void freeScope() {
        sb.append(scope.freeAll());
        scope = scope.parent;
    }
}
