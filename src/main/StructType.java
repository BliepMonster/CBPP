package main;


import java.util.Map;
import java.util.Objects;

public class StructType extends VariableType {
    public final Map<String, VariableType> fields;

    public StructType(Map<String, VariableType> fields) {
        this.fields = fields;
    }

    @Override
    public int getSize() {
        int s = 0;
        for (VariableType type : fields.values())
            s += type.getSize();
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StructType that = (StructType) o;
        return Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fields);
    }
}
