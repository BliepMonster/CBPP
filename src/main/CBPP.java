package main;

import compilation.PrettyPrinter;
import compilation.ir.Compiler;
import compilation.ir.instructions.Instruction;
import compilation.lowering.Lowerer;
import statements.Statement;

import java.io.FileInputStream;
import java.io.IOException;
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
        ArrayList<Instruction> instructions = new Compiler().compile(stmts);
        instructions = Lowerer.lower(instructions);
        new PrettyPrinter().print(instructions);
    }
}
