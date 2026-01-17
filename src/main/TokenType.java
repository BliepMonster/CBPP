package main;

public enum TokenType {
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

