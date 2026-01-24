package main;

public class ByteType extends VariableType {
    public static final ByteType INSTANCE = new ByteType();
    public int getSize() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ByteType;
    }

    @Override
    public String toString() {
        return "byte";
    }
}
