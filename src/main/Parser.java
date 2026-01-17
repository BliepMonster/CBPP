package main;

import expressions.*;
import statements.ExpressionStatement;
import statements.PrintStatement;
import statements.Statement;

import java.util.ArrayList;
import java.util.List;

import static main.TokenType.*;
class ParserException extends RuntimeException {
    public ParserException(String message, int line) {
        super(message+" (line "+line+")");
    }
}

public class Parser {
    private final ArrayList<Token> tokens;
    private int index;
    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

    public List<Statement> parse() {
        List<Statement> stmts = new ArrayList<>();
        while (!isAtEnd())
            stmts.add(statement());
        return stmts;
    }
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }
    private boolean isAtEnd() {
        return peek().type == EOF;
    }
    private Token advance() {
        if (!isAtEnd()) index++;
        return previous();
    }
    private Token peek() {
        return tokens.get(index);
    }
    private Token previous() {
        return tokens.get(index - 1);
    }
    public Expression expression() {
        return assignment();
    }
    public Expression assignment() {
        Expression expr = xor();
        if (match(EQ)) {
            Token op = previous();
            Expression right = assignment();
            if (!(expr instanceof IdentifierExpression))
                throw new ParserException("Invalid assignment", op.line);
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }
    public Expression xor() {
        Expression expr = or();
        while (match(XOR)) {
            Token op = previous();
            Expression right = or();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }
    public Expression or() {
        Expression expr = and();
        while (match(OR)) {
            Token op = previous();
            Expression right = and();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }
    public Expression and() {
        Expression expr = equality();
        while (match(AND)) {
            Token op = previous();
            Expression right = equality();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }
    public Expression equality() {
        Expression expr = comparison();
        while (match(EQEQ, NEQ)) {
            Token op = previous();
            Expression right = comparison();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }
    public Expression comparison() {
        Expression expr = term();
        while (match(GT, LT, GTEQ, LTEQ)) {
            Token op = previous();
            Expression right = term();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }
    public Expression term() {
        Expression expr = factor();
        while (match(PLUS, MINUS)) {
            Token op = previous();
            Expression right = factor();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }
    public Expression factor() {
        Expression expr = unary();
        while (match(STAR, SLASH, MOD)) {
            Token op = previous();
            Expression right = unary();
            expr = new BinaryExpression(expr, op, right);
        }
        return expr;
    }
    public Expression unary() {
        if (match(MINUS, BANG, QUESTION)) {
            Token op = previous();
            Expression r = exponent();
            return new UnaryExpression(r, op);
        }
        return exponent();
    }
    public Expression exponent() {
        Expression expr = primary();
        if (match(EXPONENT)) {
            Token op = previous();
            Expression r = unary();
            expr = new BinaryExpression(expr, op, r);
        }
        return expr;
    }
    public Expression primary() {
        if (match(TRUE)) return new LiteralExpression(true);
        if (match(FALSE)) return new LiteralExpression(false);
        if (match(NUM)) return new LiteralExpression(Integer.parseInt(previous().text));
        if (match(STRING)) return new LiteralExpression(parseStr(previous().text));
        if (match(CHAR)) return new LiteralExpression(previous().text.charAt(1));
        if (match(IDENTIFIER)) return new IdentifierExpression(previous().text);
        if (match(LPAREN)) {
            Expression expr = expression();
            consume(RPAREN, "Expect ')' after grouping expression.");
            return expr;
        }
        throw new ParserException("Expect expression.", peek().line);
    }
    public void consume(TokenType t, String error) {
        if (!match(t)) {
            throw new ParserException(error, peek().line);
        }
    }
    public String parseStr(String s) {
        return s.substring(1, s.length()-1);
    }

    public void consumeSemicolon() {
        consume(SEMICOLON, "Expected semicolon.");
    }


    public Statement statement() {
        if (match(PRINT))
            return printStatement();
        else
            return expressionStatement();
    }
    public Statement printStatement() {
        Statement value = new PrintStatement(expression());
        consumeSemicolon();
        return value;
    }
    public Statement expressionStatement() {
        ExpressionStatement expressionStatement = new ExpressionStatement(expression());
        consumeSemicolon();
        return expressionStatement;
    }
}
