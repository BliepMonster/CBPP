package statements;

import main.Variable;

import java.util.ArrayList;

public class StructStatement extends Statement {
    public final String name;
    public final ArrayList<Variable> variables;

    public StructStatement(String name, ArrayList<Variable> variables) {
        this.name = name;
        this.variables = variables;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitStructStatement(this);
    }
}
