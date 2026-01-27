package compilation.bpp;

import java.util.ArrayList;

public final class StructRegister extends Register {
    private final ArrayList<SimpleRegister> regs;

    public StructRegister(ArrayList<SimpleRegister> regs) {
        this.regs = regs;
    }
    public SimpleRegister get(int i) {
        return regs.get(i);
    }
    public int length() {
        return regs.size();
    }
}
