package main;

import java.util.ArrayList;
import java.util.HashMap;

class PreprocessorException extends RuntimeException {
    public PreprocessorException(String text) {
        super(text);
    }
}

public class Preprocessor {
    private ArrayList<Token> tokens;
    private ArrayList<Token> newTokens;
    private int index = 0;
    private HashMap<Token, ArrayList<Token>> macros = new HashMap<>();
    public Preprocessor(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }
    public ArrayList<Token> execute() {
        /*
        PLAN: CREATE NEW LIST, FOREACH TOKEN: IF TOKEN == COMMAND, EXECUTE COMMAND
        ELSE, ADD TO LIST (MACRO)
         */
        newTokens = new ArrayList<>();
        while (!isAtEnd()) {
            executeOnce();
        }
        newTokens.add(tokens.getLast());
        return newTokens;
    }
    public boolean isAtEnd() {
        return tokens.get(index).type == TokenType.EOF;
    }
    public void executeOnce() {
        Token t = advance();
        switch(t.type) {
            case NATIVE: {
                Token text = advance();
                if (text.type != TokenType.STRING)
                    throw new PreprocessorException("INVALID NATIVE");
                newTokens.add(new Token(text.text.substring(1, text.text.length()-1), TokenType.NATIVE_CODE, text.line));
                consume(TokenType.SEMICOLON);
                break;
            }
            case DEFINE: {
                Token definition = advance();
                Token result = advance();
                if (result.type != TokenType.STRING)
                    throw new PreprocessorException("INVALID MACRO");
                String text = result.text.substring(1, result.text.length()-1);
                ArrayList<Token> res = new Scanner(text).scan();
                res.removeLast();
                macros.put(definition, res);
                consume(TokenType.SEMICOLON);
                break;
            }
            case UNDEF: {
                Token macro = advance();
                macros.remove(macro);
                consume(TokenType.SEMICOLON);
                break;
            }
            case IFDEF: {
                Token macro = advance();
                boolean contains = macros.containsKey(macro);
                consume(TokenType.LBRACE);
                int depth = 1;
                while (depth != 0) {
                    if (tokens.get(index).type == TokenType.LBRACE)
                        depth++;
                    else if (tokens.get(index).type == TokenType.RBRACE)
                        if (depth == 1) {
                            advance();
                            break;
                        } else
                            depth--;
                    if (contains)
                        executeOnce();
                    if (isAtEnd())
                        throw new PreprocessorException("Unterminated IFDEF block");
                    else advance();
                }
                break;
            }
            case IFNDEF: {
                Token macro = advance();
                boolean contains = !macros.containsKey(macro);
                consume(TokenType.LBRACE);
                int depth = 1;
                while (depth != 0) {
                    if (tokens.get(index).type == TokenType.LBRACE)
                        depth++;
                    else if (tokens.get(index).type == TokenType.RBRACE)
                        if (depth == 1) {
                            advance();
                            break;
                        } else
                            depth--;
                    if (contains)
                        executeOnce();
                    if (isAtEnd())
                        throw new PreprocessorException("Unterminated IFNDEF block");
                    else advance();
                }
                break;
            }
            default: {
                ArrayList<Token> macro = macros.get(t);
                if (macro == null)
                    newTokens.add(t);
                else
                    newTokens.addAll(macro);
                break;
            }
        }
    }
    public boolean match(TokenType type) {
        if (tokens.get(index).type == type) {
            advance();
            return true;
        } return false;
    }
    public Token advance() {
        if (isAtEnd()) throw new PreprocessorException("IS AT END");
        return tokens.get(index++);
    }
    public void consume(TokenType t) {
        Token token = advance();
        if (token.type != t)
            throw new PreprocessorException("EXPECTED "+t);
    }
}
