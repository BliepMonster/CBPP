package statements;

import main.FunctionArgument;

import java.util.ArrayList;

public class FunctionStatement extends Statement {
    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitFunctionStatement(this);
    }
    public final String name;
    public final ArrayList<FunctionArgument> args;
    public final ArrayList<Statement> body;

    public FunctionStatement(String name, ArrayList<FunctionArgument> args, ArrayList<Statement> body) {
        this.name = name;
        this.args = args;
        this.body = body;
    }
}
