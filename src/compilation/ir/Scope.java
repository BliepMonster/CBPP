package compilation.ir;

import compilation.CompiledVariable;
import compilation.ir.instructions.AllocateInstruction;
import compilation.ir.instructions.FreeInstruction;
import compilation.ir.instructions.Instruction;

import java.util.ArrayList;
import java.util.HashMap;

public class Scope {
    public Scope parent = null;
    private HashMap<String, UniqueVariable> variables = new HashMap<>();
    private int lastAllocatedID = 0;
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
}
