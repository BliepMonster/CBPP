package main;

import expressions.*;
import statements.*;

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
        if (match(LPAREN)) {
            return grouping();
        }
        return identifier();
    }
    public Expression grouping() {
        Expression expr = expression();
        consume(RPAREN, "Expect ')' after grouping expression.");
        return expr;
    }
    public Expression identifier() {
        consume(IDENTIFIER, "Expected identifier");
        String name = previous().text;
        Expression expr;
        if (match(LPAREN)) {
            ArrayList<Expression> args = new ArrayList<>();
            if (match(RPAREN))
                return new CallExpression(name, args);
            args.add(expression());
            while (!match(RPAREN)) {
                consume(COMMA, "Expected comma");
                args.add(expression());
            }
            expr = new CallExpression(name, args);
        } else
            expr = new IdentifierExpression(name);
        while (peek().type == DOT)
            expr = matchDot(expr);
        return expr;
    }
    public Expression matchDot(Expression initial) {
        if (match(DOT)) {
            return new DotExpression(initial, consume(IDENTIFIER, "Expected identifier").text);
        } return initial;
    }
    public Token consume(TokenType t, String error) {
        if (!match(t)) {
            throw new ParserException(error, peek().line);
        } return previous();
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
        else if (match(VAR))
            return varStatement();
        else if (match(LBRACE))
            return blockStatement();
        else if (match(IF))
            return ifStatement();
        else if (match(WHILE))
            return whileStatement();
        else if (match(NATIVE_CODE))
            return nativeStatement();
        else if (match(MFN))
            return functionDeclarationStatement();
        else if (match(STRUCT))
            return structDeclarationStatement();
        else if (match(RETURN))
            return returnStatement();
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
    public Statement varStatement() {
        String assigned = consume(IDENTIFIER, "Expected identifier").text;
        consume(COLON, "Expected ':'");
        String typeName = consume(IDENTIFIER, "Expected IDENTIFIER").text;
        consume(EQ, "Expected '='");
        Expression assignment = expression();
        consumeSemicolon();
        return new VarStatement(assigned, assignment, typeName);
    }
    public Statement blockStatement() {
        ArrayList<Statement> statements = new ArrayList<>();
        while (!match(RBRACE)) {
            if (isAtEnd())
                throw new ParserException("Invalid block termination", peek().line);
            statements.add(statement());
        }
        return new BlockStatement(statements);
    }
    public Statement ifStatement() {
        consume(LPAREN, "Expected '('");
        Expression expr = expression();
        consume(RPAREN, "Expected ')'");
        Statement stmt1 = statement();
        Statement stmt2;
        if (match(ELSE))
            stmt2 = statement();
        else
            stmt2 = null;
        return new IfStatement(expr, stmt1, stmt2);
    }
    public Statement whileStatement() {
        consume(LPAREN, "Expected '('");
        Expression expr = expression();
        consume(RPAREN, "Expected ')'");
        Statement stmt = statement();
        return new WhileStatement(expr, stmt);
    }
    public Statement nativeStatement() {
        return new NativeStatement(previous().text);
    }
    public Statement functionDeclarationStatement() {
        String name = consume(IDENTIFIER, "Expected IDENTIFIER as function name").text;
        consume(LPAREN, "Expected '('");
        ArrayList<VariableSymbol> args = new ArrayList<>();
        if (match(RPAREN)) {
            String rtype = returnType();
            return new FunctionStatement(name, args, functionBody(), rtype);
        }
        args.add(functionArgument());
        while (!match(RPAREN)) {
            consume(COMMA, "Expected comma");
            args.add(functionArgument());
        }
        String rtype = returnType();
        return new FunctionStatement(name, args, functionBody(), rtype);
    }
    public VariableSymbol functionArgument() {
        String name = consume(IDENTIFIER, "Expected IDENTIFIER").text;
        consume(COLON, "Expected ':'");
        String type = consume(IDENTIFIER, "Expected IDENTIFIER").text;
        return new VariableSymbol(name, type);
    }
    public ArrayList<Statement> functionBody() {
        if (match(LBRACE)) {
            ArrayList<Statement> stmts = new ArrayList<>();
            while (!match(RBRACE))
                stmts.add(statement());
            return stmts;
        } else if (match(COLON)) {
            ArrayList<Statement> stmts = new ArrayList<>();
            stmts.add(new ReturnStatement(expression()));
            consumeSemicolon();
            return stmts;
        }
        throw new ParserException("Expected '{' or ':'", previous().line);
    }
    public String returnType() {
        if (match(ARROW)) {
            return consume(IDENTIFIER, "Expected RETURN TYPE").text;
        }
        return null;
    }
    public Statement structDeclarationStatement() {
        String name = consume(IDENTIFIER, "Expected IDENTIFIER").text;
        consume(LBRACE, "Expected '{'");
        ArrayList<Variable> fields = new ArrayList<>();
        // AT LEAST ONE FIELD
        do {
            fields.add(structField());
        } while (!match(RBRACE));
        return new StructStatement(name, fields);
    }
    public Variable structField() {
        String name = consume(IDENTIFIER, "Expected IDENTIFIER").text;
        consume(COLON, "Expected ':'");
        String type = consume(IDENTIFIER, "Expected IDENTIFIER").text;
        consumeSemicolon();
        return new Variable(name, type);
    }
    public Statement returnStatement() {
        Expression expr = expression();
        consumeSemicolon();
        return new ReturnStatement(expr);
    }
}
