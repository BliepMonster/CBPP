package main;

import compilation.verification.Struct;

import java.util.Map;

public class StructType extends VariableType {
    public final Map<String, VariableType> fields;
    public final Struct struct;

    public StructType(Struct struct, Map<String, VariableType> fields) {
        this.struct = struct;
        this.fields = fields;
    }

    @Override
    public int getSize() {
        int s = 0;
        for (VariableType type : fields.values())
            s += type.getSize();
        return s;
    }
}
