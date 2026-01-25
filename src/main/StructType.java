package main;


import compilation.ir.StructField;

import java.util.HashMap;
import java.util.Objects;

class StructLookupException extends RuntimeException {
    public StructLookupException(String s) {
        super(s);
    }
}

public class StructType extends VariableType {
    public final HashMap<StructField, Integer> fields;

    public StructType(HashMap<StructField, Integer> fields) {
        this.fields = fields;
    }

    @Override
    public int getSize() {
        int s = 0;
        for (StructField field : fields.keySet())
            s += field.type().getSize();
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
    public String toString() {
        StringBuilder sb = new StringBuilder("STRUCT_[");
        for (StructField f : fields.keySet()) {
            sb.append(f.type()).append(",");
        }
        return sb.append("]").toString();
    }
    public int getFieldOffset(String s) {
        for (StructField field : fields.keySet()) {
            if (field.name().equals(s))
                return fields.get(field);
        }
        throw new StructLookupException("Invalid field");
    }
    public VariableType getType(int offset) {
        for (StructField field : fields.keySet()) {
            if (fields.get(field) == offset)
                return field.type();
        }
        throw new StructLookupException("Invalid field offset");
    }
}
