package compilation.bpp;

public final class SimpleRegister extends Register {
    public final int pos;
    public SimpleRegister(int pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "r"+pos;
    }
}
