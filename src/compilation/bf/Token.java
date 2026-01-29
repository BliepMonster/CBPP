package compilation.bf;

public class Token {
    public final String token;
    public final TokenType type;
    public final int line;

    public Token(String token, TokenType type, int line) {
        this.token = token;
        this.type = type;
        this.line = line;
    }
    public String toString() {
        return "{"+type+", "+token+", "+line+"}";
    }
}
enum TokenType {
    PUT,
    CLEAR,
    INC,
    DEC,
    ADD,
    SUB,
    COPY,
    SWAP,
    WHILE,
    PRINT,
    INPUT,
    PRINTC,
    PRINTSTR,
    PRINTLN,
    VPRINT_U,
    MUL,
    INV,
    NAT,
    EQ,
    DEF,
    RDEF,
    UNDEF,
    UNRDEF,
    IFDEF,
    IFRDEF,
    CDEF,
    UNCDEF,
    IFCDEF,
    IMPORT,
    DIVMOD,
    MOVE,
    BITOR,
    BITAND,
    BITXOR,

    RNAME, // register
    LITERAL,

    OTHER,

    COMMA,
    LBRACE,
    RBRACE,
    SEMICOLON,

    CHAR_CONST,
    STRING_CONST,

    EOF
}