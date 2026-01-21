package compilation.ir;

import main.VariableType;

public record UniqueVariable(String name, int identifier, VariableType type) {
    public String getUniqueName() {
        return name+"_"+identifier;
    }
}
