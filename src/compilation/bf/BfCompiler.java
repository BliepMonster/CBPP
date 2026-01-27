package compilation.bf;

public class BfCompiler {
    public String compile(String src) {
        return "++++++++++++++++++++++++++++++++"+new Compiler().compile(new Scanner(src).scan());
    }
}
