package main;

import expressions.Expression;

import java.io.FileInputStream;
import java.util.ArrayList;

public class CBPP {
    public static void main(String[] args) {
        String input = args[0];
        try (FileInputStream in = new FileInputStream(input)) {
            String src = new String(in.readAllBytes());
            ArrayList<Token> tokens = new Scanner(src).scan();
            tokens = new Preprocessor(tokens).execute();
            Expression expr = new Parser(tokens).expression();
            new PrettyPrinter().print(expr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
