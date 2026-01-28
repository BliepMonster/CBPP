package main;

public enum TokenType {
    VAR,
    PRINT,
    STRUCT,
    WHILE,
    IF,
    ELSE,
    RETURN,
    MFN,
    $INPUT,

    DEFINE,
    NATIVE,
    IFDEF,
    UNDEF,
    IFNDEF,
    IMPORT,

    NUM,
    TRUE,
    FALSE,
    CHAR,
    STRING,
    NATIVE_CODE,

    IDENTIFIER,

    PLUS,
    MINUS,
    ARROW,
    STAR,
    SLASH,
    EXPONENT,
    MOD,
    BANG,
    OR,
    AND,
    XOR,
    QUESTION,
    COLON,

    EQ,
    EQEQ,
    NEQ,
    GT,
    GTEQ,
    LT,
    LTEQ,

    PLUS_EQ,
    MINUS_EQ,
    STAR_EQ,
    SLASH_EQ,
    EXP_EQ,
    MOD_EQ,
    AND_EQ,
    OR_EQ,
    XOR_EQ,

    SEMICOLON,
    COMMA,
    LBRACE,
    RBRACE,
    LPAREN,
    RPAREN,
    DOT,

    EOF
}

