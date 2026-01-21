package main;

import expressions.Expression;
import statements.Statement;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class CBPP {
    public static void main(String[] args) {
        String input = args[0];
        try (FileInputStream in = new FileInputStream(input)) {
            String src = new String(in.readAllBytes());
            ArrayList<Token> tokens = new Scanner(src).scan();
            tokens = new Preprocessor(tokens).execute();
            List<Statement> stmts = new Parser(tokens).parse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
