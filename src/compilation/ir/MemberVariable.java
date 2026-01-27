package compilation.ir;

import main.VariableType;

public class MemberVariable extends UniqueVariable {
    public final int offset;

    public MemberVariable(String name, int identifier, VariableType type, int offset) {
        super(name, identifier, type);
        this.offset = offset;
    }

    @Override
    public String getUniqueName() {
        return super.getUniqueName()+"["+offset+"]";
    }
    public String getOwnerName() {
        return super.getUniqueName();
    }
}
