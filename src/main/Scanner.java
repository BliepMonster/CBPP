package main;

import java.util.ArrayList;

class ScanException extends RuntimeException {
    public ScanException(String s, int line) {
        super("Error at line "+line+": "+s);
    }
}

public class Scanner {
    private final String code;
    private final ArrayList<Token> tokens = new ArrayList<>();
    private int pointer, start;
    private int line = 0;
    public Scanner(String source) {
        this.code = source;
    }
    public ArrayList<Token> scan() {
        while (!isAtEnd()) {
            scanOnce();
        }
        tokens.add(new Token("", TokenType.EOF, line));
        return tokens;
    }
    public void scanOnce() {
        skipWhitespace();
        if (isAtEnd()) return;
        start = pointer;
        char c = advance();
        switch (c) {
            case '{': tokens.add(makeToken(TokenType.LBRACE));
                break;
            case '}': tokens.add(makeToken(TokenType.RBRACE));
                break;
            case '(': tokens.add(makeToken(TokenType.LPAREN));
                break;
            case ')': tokens.add(makeToken(TokenType.RPAREN));
                break;
            case ';': tokens.add(makeToken(TokenType.SEMICOLON));
                break;
            case ',': tokens.add(makeToken(TokenType.COMMA));
                break;
            case '.': tokens.add(makeToken(TokenType.DOT));
                break;
            case '\'':
                advance();
                consume('\'', "Expected char literal.");
                tokens.add(makeToken(TokenType.CHAR));
                break;
            case '"':
                while (peek() != '"' && !isAtEnd()) {
                    char ch = peek();
                    if (ch == '\n') line++;
                    advance();
                }
                if (isAtEnd()) throw new ScanException("Invalid string.", line);
                advance();
                tokens.add(makeToken(TokenType.STRING));
                break;
            case '+': tokens.add(makeToken(TokenType.PLUS));
                break;
            case '-': tokens.add(makeToken(TokenType.MINUS));
                break;
            case '*': tokens.add(makeToken(match('*') ? TokenType.EXPONENT : TokenType.STAR));
                break;
            case '/': tokens.add(makeToken(TokenType.SLASH));
                break;
            case '%': tokens.add(makeToken(TokenType.MOD));
                break;
            case '!': tokens.add(makeToken(match('=') ? TokenType.NEQ : TokenType.BANG));
                break;
            case '?': tokens.add(makeToken(TokenType.QUESTION));
                break;
            case '>': tokens.add(makeToken(match('=') ? TokenType.GTEQ : TokenType.GT));
                break;
            case '<': tokens.add(makeToken(match('=') ? TokenType.LTEQ : TokenType.LT));
                break;
            case '=': tokens.add(makeToken(match('=') ? TokenType.EQEQ : TokenType.EQ));
                break;
            case '|': tokens.add(makeToken(TokenType.OR));
                break;
            case '^': tokens.add(makeToken(TokenType.XOR));
                break;
            case '&': tokens.add(makeToken(TokenType.AND));
                break;
            case '\0': break;
            default:
                if (isNumeric(c)) {
                    number();
                    break;
                }
                else if (isAlpha(c)) {
                    identifier();
                    break;
                }
                else throw new ScanException("Invalid token: "+c, line);
        }
    }
    public boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }
    public void identifier() {
        while (isAlpha(peek()) || isNumeric(peek())) {
            advance();
        }
        tokens.add(makeToken(idenType()));
    }
    public TokenType idenType() {
        String s = code.substring(start, pointer);
        return switch(s) {
            case "true" -> TokenType.TRUE;
            case "false" -> TokenType.FALSE;
            case "var" -> TokenType.VAR;
            case "while" -> TokenType.WHILE;
            case "mfn" -> TokenType.MFN;
            case "printc" -> TokenType.PRINTC;
            case "printb" -> TokenType.PRINTB;
            case "printv" -> TokenType.PRINTV;
            case "printstr" -> TokenType.PRINTSTR;
            case "struct" -> TokenType.STRUCT;
            case "this" -> TokenType.THIS;
            case "if" -> TokenType.IF;
            case "else" -> TokenType.ELSE;
            case "return" -> TokenType.RETURN;
            case "define" -> TokenType.DEFINE;
            case "undef" -> TokenType.UNDEF;
            case "ifdef" -> TokenType.IFDEF;
            case "ifndef" -> TokenType.IFNDEF;
            case "native" -> TokenType.NATIVE;
            default -> TokenType.IDENTIFIER;
        };
    }
    public void number() {
        while (isNumeric(peek())) {
            advance();
        }
        tokens.add(makeToken(TokenType.NUM));
    }
    public boolean isNumeric(char c) {
        return c >= '0' && c <= '9';
    }
    public boolean match(char c) {
        if (peek() == c) {
            advance();
            return true;
        }
        return false;
    }
    public void consume(char c, String error) {
        char a = advance();
        if (a != c)
            throw new ScanException(error, line);
    }
    public Token makeToken(TokenType type) {
        return new Token(code.substring(start, pointer), type, line);
    }
    public boolean isAtEnd() {
        return pointer >= code.length();
    }
    public char advance() {
        if (isAtEnd()) return '\0';
        return code.charAt(pointer++);
    }
    public char peek() {
        if (isAtEnd()) return '\0';
        return code.charAt(pointer);
    }
    public void skipWhitespace() {
        boolean loop = true;
        while (loop) {
            char c = peek();
            switch (c) {
                case '\n':
                    line++;
                    advance();
                    break;
                case ' ', '\t', '\r':
                    advance();
                    break;
                case'@':
                    while (peek() != '\n' && !isAtEnd()) advance();
                    break;
                default:
                    loop = false;
            }
        }
    }
}
