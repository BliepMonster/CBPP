package compilation.ir;

import main.VariableType;

public class UniqueVariable {
    public final String name;
    public final int identifier;
    public final VariableType type;
    public String getUniqueName() {
        return name+"_"+identifier;
    }

    public UniqueVariable(String name, int identifier, VariableType type) {
        this.name = name;
        this.identifier = identifier;
        this.type = type;
    }
}
