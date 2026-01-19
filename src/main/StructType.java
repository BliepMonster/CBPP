package main;

import java.util.Map;

public class StructType extends VariableType {
    public final String name;
    public final Map<String, VariableType> fields;

    public StructType(String name, Map<String, VariableType> fields) {
        this.name = name;
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
