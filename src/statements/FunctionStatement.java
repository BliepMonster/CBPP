package statements;

import main.VariableSymbol;

import java.util.ArrayList;

public class FunctionStatement extends Statement {
    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitFunctionStatement(this);
    }
    public final String name, returnType;
    public final ArrayList<VariableSymbol> args;
    public final ArrayList<Statement> body;

    public FunctionStatement(String name, ArrayList<VariableSymbol> args, ArrayList<Statement> body, String returnType) {
        this.name = name;
        this.args = args;
        this.body = body;
        this.returnType = returnType;
    }
}
