package compilation.bpp;

import java.util.*;

public class VariableSpace {
    private final HashMap<String, Register> regs = new HashMap<>();
    public Register allocate(String name, int size, boolean isStruct) {
        if (!isStruct)
            return allocSingle(name);
        ArrayList<SimpleRegister> parts = new ArrayList<>();
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            int q = findNextEmpty(set);
            set.add(q);
            parts.add(new SimpleRegister(q));
        }
        Register r = new StructRegister(parts);
        regs.put(name, r);
        return r;
    }
    private SimpleRegister allocSingle(String name) {
        int reg = findNextEmpty(new HashSet<>());
        SimpleRegister r = new SimpleRegister(reg);
        regs.put(name, r);
        return r;
    }
    public void remove(String name) {
        regs.remove(name);
    }
    private int findNextEmpty(Set<Integer> all) {
        Set<Integer> allocated = new HashSet<>(all);
        for (Register r : regs.values()) {
            switch (r) {
                case SimpleRegister si -> allocated.add(si.pos);
                case StructRegister st -> {
                    for (int i = 0; i < st.length(); i++) {
                        allocated.add(st.get(i).pos);
                    }
                }
            }
        }
        for (int i = IrCompiler.NUM_SPECIAL_REGS;;i++) {
            if (!allocated.contains(i))
                return i;
        }
    }
    public Register get(String name) {
        return regs.get(name);
    }
}
