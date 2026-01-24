package compilation.ir;

public class FunctionScope extends Scope {
    private UniqueVariable returnVar;
    public FunctionScope(Scope parent, UniqueFunction fn) {
        super(parent);
        this.returnVar = fn.result();
        for (FunctionArgument arg : fn.params()) {
            nofree.put(arg.name(), arg.position());
        }
    }

    public UniqueVariable getReturnVar() {
        return returnVar;
    }
}
