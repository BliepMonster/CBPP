package main;

import compilation.bf.BfCompiler;
import compilation.bpp.IrCompiler;
import compilation.ir.Compiler;
import compilation.ir.instructions.Instruction;
import compilation.lowering.Lowerer;
import optimization.ConstantFolder;
import statements.Statement;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CBPP {
    public static void main(String[] args) throws IOException {
        String input = args[0];
        FileInputStream in = new FileInputStream(input);
        String src = new String(in.readAllBytes());
        ArrayList<Token> tokens = new Scanner(src).scan();
        tokens = new Preprocessor(tokens).execute();
        List<Statement> stmts = new Parser(tokens).parse();

        stmts = new ConstantFolder().optimize(stmts);

        ArrayList<Instruction> instructions = new Compiler().compile(stmts);
        instructions = Lowerer.lower(instructions);

        String output = args[1];
        FileOutputStream out = new FileOutputStream(output);
        PrintStream stream = new PrintStream(out);
        String bpp = new IrCompiler().compile(instructions);
        stream.print(new BfCompiler().compile(bpp));
        stream.close();
    }
}
