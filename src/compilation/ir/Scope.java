package compilation.ir;

import compilation.ir.instructions.mem.AllocateInstruction;
import compilation.ir.instructions.mem.FreeInstruction;
import compilation.ir.instructions.Instruction;
import main.StructType;
import main.VariableType;

import java.util.ArrayList;
import java.util.HashMap;

class ScopingException extends RuntimeException {
    public ScopingException(String name) {
        super(name);
    }
}

public class Scope {
    public Scope parent;
    protected HashMap<String, UniqueVariable> variables = new HashMap<>();
    protected HashMap<String, UniqueVariable> nofree = new HashMap<>();
    protected HashMap<String, StructType> structs = new HashMap<>();
    protected HashMap<String, UniqueFunction> functions = new HashMap<>();
    private static int  lastAllocatedID = 0,
                        lastTempID = 0;
    public Scope(Scope parent) {
        this.parent = parent;
    }
    public Instruction register(CompiledVariable variable) {
        UniqueVariable v = new UniqueVariable(variable.name(), lastAllocatedID++, variable.type());
        variables.put(variable.name(), v);
        return new AllocateInstruction(v.getUniqueName(), v.type.getSize());
    }
    public TempAllocationResult allocTemp(VariableType type) {
        int i = lastTempID++;
        String name = "@temp_"+i;
        Instruction instr = new AllocateInstruction(name, type.getSize());
        variables.put(name, new UniqueVariable(name, i, type));
        return new TempAllocationResult(name, instr);
    }
    public ArrayList<Instruction> freeAll() {
        ArrayList<Instruction> instructions = new ArrayList<>();
        for (UniqueVariable var : variables.values()) {
            instructions.add(new FreeInstruction(var.getUniqueName()));
        }
        variables.clear();
        structs.clear();
        return instructions;
    }
    public UniqueVariable retrieveVar(String name) {
        if (this.variables.containsKey(name))
            return this.variables.get(name);
        else if (this.nofree.containsKey(name))
            return this.nofree.get(name);
        if (parent == null)
            throw new ScopingException("Variable doesn't exist: "+name);
        UniqueVariable var = parent.retrieveVar(name);
        if (var == null)
            throw new ScopingException("Variable doesn't exist: "+name);
        return var;
    }
    public StructType retrieveStruct(String name) {
        if (this.structs.containsKey(name))
            return this.structs.get(name);
        if (parent == null)
            throw new ScopingException("Struct doesn't exist: "+name);
        StructType var = parent.retrieveStruct(name);
        if (var == null)
            throw new ScopingException("Struct doesn't exist: "+name);
        return var;
    }
    public void register(CompiledStruct struct) {
        StructType str = struct.struct();
        structs.put(struct.name(), str);
    }
    public void register(UniqueFunction fn) {
        functions.put(fn.getUniqueName(), fn);
    }
    public UniqueFunction retrieveFunction(FunctionRecord fn) {
        ArrayList<String> typenames = new ArrayList<>();
        for (VariableType type : fn.args()) {
            typenames.add(type.toString());
        }
        String uname = Compiler.getFunctionUniqueName(fn.name(), typenames);
        return retrieveFunctionByName(uname);
    }
    protected UniqueFunction retrieveFunctionByName(String uname) {
        UniqueFunction ufn = functions.get(uname);
        if (ufn == null && parent != null) {
            return parent.retrieveFunctionByName(uname);
        } else if (parent == null && ufn == null) {
            throw new ScopingException("FUNCTION NOT FOUND: "+uname);
        }
        return ufn;
    }
    public Instruction freeVar(String name) {
        UniqueVariable uv = variables.get(name);
        variables.remove(name);
        return new FreeInstruction(uv.name);
    }
}
