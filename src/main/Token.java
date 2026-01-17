package main;

public class Token {
    public final String text;
    public final TokenType type;
    public final int line;
    public Token(String token, TokenType type, int line) {
        this.text = token;
        this.type = type;
        this.line = line;
    }
    public String toString() {
        return "{"+type+", "+text+", "+line+"}";
    }
    public int hashCode() {
        return this.text.trim().hashCode();
    }
    public boolean equals(Object o) {
        if (o instanceof Token t)
            return this.text.trim().equals(t.text.trim());
        return false;
    }
}