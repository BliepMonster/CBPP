package compilation.bf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BfCompiler {
    public void compile(String src, FileOutputStream out) throws IOException {
        //out.write(("++++++++++++++++++++++++++++++++"+new Compiler().compile(new Scanner(src).scan())).getBytes());
        ArrayList<Byte> bytes = new BytecodeCompiler().compile(new Scanner(src).scan());
        System.out.println(bytes);
        for (byte b : bytes) {
            out.write(b);
        }
    }
}
