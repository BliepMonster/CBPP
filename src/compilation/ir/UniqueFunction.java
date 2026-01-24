package compilation.ir;

import java.util.ArrayList;

public record UniqueFunction(String name, ArrayList<FunctionArgument> params, UniqueVariable result) {
    public String getUniqueName() {
        ArrayList<String> typeNames = new ArrayList<>();
        for (FunctionArgument fa : params) {
            typeNames.add(fa.position().type.toString());
        }
        return Compiler.getFunctionUniqueName(name, typeNames);
    }
}
