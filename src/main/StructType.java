package main;


import compilation.ir.StructField;

import java.util.ArrayList;
import java.util.Objects;

class StructLookupException extends RuntimeException {
    public StructLookupException(String s) {
        super(s);
    }
}

public class StructType extends VariableType {
    public final ArrayList<StructField> fields;

    public StructType(ArrayList<StructField> fields) {
        this.fields = fields;
    }

    @Override
    public int getSize() {
        int s = 0;
        for (StructField field : fields)
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
        for (StructField f : fields) {
            sb.append(f.type()).append(",");
        }
        return sb.append("]").toString();
    }
    public int getFieldOffset(String s) {
        for (int i = 0; i < fields.size(); i++) {
            if (s.equals(fields.get(i).name()))
                return i;
        }
        throw new StructLookupException("Invalid field");
    }
    public VariableType getType(int offset) {
        return fields.get(offset).type();
    }
}
