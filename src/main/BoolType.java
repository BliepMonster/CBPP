package main;

public class BoolType extends VariableType {
    public static final BoolType INSTANCE = new BoolType();
    public int getSize() {
        return 1;
    }
    @Override
    public String toString() {
        return "bool";
    }
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BoolType;
    }
}
