package compilation.ir;

import compilation.CompiledVariable;
import compilation.ir.instructions.AllocateInstruction;
import compilation.ir.instructions.FreeInstruction;
import compilation.ir.instructions.Instruction;
import main.StructType;

import java.util.ArrayList;
import java.util.HashMap;

class ScopingException extends RuntimeException {
    public ScopingException(String name) {
        super(name);
    }
}

public class Scope {
    public Scope parent;
    private HashMap<String, UniqueVariable> variables = new HashMap<>();
    private HashMap<String, StructType> structs = new HashMap<>();
    private int lastAllocatedID = 0;
    public Scope(Scope parent) {
        this.parent = parent;
    }
    public Instruction register(CompiledVariable variable) {
        UniqueVariable v = new UniqueVariable(variable.name(), lastAllocatedID++, variable.type());
        variables.put(variable.name(), v);
        return new AllocateInstruction(v.getUniqueName(), v.type().getSize());
    }
    public ArrayList<Instruction> freeAll() {
        ArrayList<Instruction> instructions = new ArrayList<>();
        for (UniqueVariable var : variables.values()) {
            instructions.add(new FreeInstruction(var.getUniqueName()));
        }
        variables.clear();
        return instructions;
    }
    public UniqueVariable retrieveVar(String name) {
        if (this.variables.containsKey(name))
            return this.variables.get(name);
        UniqueVariable var = parent.retrieveVar(name);
        if (var == null)
            throw new ScopingException("Variable doesn't exist: "+name);
        return var;
    }
    public StructType retrieveStruct(String name) {
        if (this.structs.containsKey(name))
            return this.structs.get(name);
        StructType var = parent.retrieveStruct(name);
        if (var == null)
            throw new ScopingException("Variable doesn't exist: "+name);
        return var;
    }
}
