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

enum TokenType {
    VAR,
    MFN,
    PRINTC,
    PRINTV,
    PRINTB,
    PRINTSTR,
    STRUCT,
    THIS,
    WHILE,
    IF,
    ELSE,
    RETURN,

    DEFINE,
    NATIVE,
    IFDEF,
    UNDEF,
    IFNDEF,

    NUM,
    TRUE,
    FALSE,
    CHAR,
    STRING,
    NATIVE_CODE,

    IDENTIFIER,

    PLUS,
    MINUS,
    STAR,
    SLASH,
    EXPONENT,
    MOD,
    BANG,
    OR,
    AND,
    XOR,
    QUESTION,

    EQ,
    EQEQ,
    NEQ,
    GT,
    GTEQ,
    LT,
    LTEQ,

    SEMICOLON,
    COMMA,
    LBRACE,
    RBRACE,
    LPAREN,
    RPAREN,
    DOT,

    EOF
}
