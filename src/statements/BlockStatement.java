package statements;

import java.util.ArrayList;

public class BlockStatement extends Statement {
    public final ArrayList<Statement> statements;
    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitBlockStatement(this);
    }

    public BlockStatement(ArrayList<Statement> statements) {
        this.statements = statements;
    }
}
