package compilation.bf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class CompilerException extends RuntimeException {
    public CompilerException(String s, Token token) {
        super("Error at line "+token.line+" at token "+token.token+": "+s);
    }
}

public class Compiler {
    static final int SPACE_REG = 0;
    static final int TEMP_REG1 = 1;
    static final int TEMP_REG2 = 2;
    static final int TEMP_REG3 = 3;
    static final int TEMP_REG4 = 4;
    static final int TEMP_REG5 = 5;
    static final int TEMP_REG6 = 6;
    static final int TEMP_REG7 = 7;
    static final int NUM_SPECIAL_REGS = 8;
    static final String V_ID = "V26.1.06";
    int rpos = 0;
    int position = 0;
    private static final HashMap<String, String>    macros = new HashMap<>(),
                                                    cmacros = new HashMap<>();
    private static final HashMap<String, Integer>   regdefs = new HashMap<>();
    ArrayList<Token> tokens;
    public String compile(ArrayList<Token> tokens) {
        this.tokens = tokens;
        StringBuilder builder = new StringBuilder();
        while (!isAtEnd()) {
            builder.append(compileStatement()).append("\n");
        }
        return builder.toString();
    }
    public String compileStatement() {
        Token current = advance();
        StringBuilder sb = new StringBuilder();
        switch (current.type) {
            case PUT: {
                Token op1 = advance();
                consume(TokenType.COMMA, "Expected comma.");
                Token r = advance();
                int rid = rvalue(r.token);
                sb.append(move(rid))
                        .append("[-]")
                        .append("+".repeat(Integer.parseInt(op1.token)));
                break;
            }
            case INC: {
                Token r = advance();
                consumeComma();
                Token op = advance();
                int rid = rvalue(r.token);
                sb.append(move(rid))
                        .append("+".repeat(Integer.parseInt(op.token)));
                break;
            }
            case DEC: {
                Token r = advance();
                consumeComma();
                Token op = advance();
                int rid = rvalue(r.token);
                sb.append(move(rid))
                        .append("-".repeat(Integer.parseInt(op.token)));
                break;
            }
            case ADD: {
                Token r1 = advance();
                consumeComma();
                Token r2 = advance();
                consumeComma();
                Token r3 = advance();
                int rid1 = rvalue(r1.token);
                int rid2 = rvalue(r2.token);
                int rid3 = rvalue(r3.token);
                // avoid adding duplicates
                if (rid1 == rid3) {
                    sb.append(move(rid2))
                            .append("[-")
                            .append(move(rid3))
                            .append("+")
                            .append(move(rid2))
                            .append("]")
                            .append(move(rid3));
                    break;
                }
                if (rid2 == rid3) {
                    sb.append(move(rid1))
                            .append("[-")
                            .append(move(rid3))
                            .append("+")
                            .append(move(rid1))
                            .append("]")
                            .append(move(rid3));
                    break;
                }
                // move rid1 to tr2
                sb.append(moveValue(rid1, TEMP_REG2));
                // clear rid3
                sb.append(move(rid3))
                        .append("[-]")
                        .append(move(TEMP_REG2));
                // move tr2 to rid1 and rid3
                sb.append("[-")
                        .append(move(rid1))
                        .append("+")
                        .append(move(rid3))
                        .append("+")
                        .append(move(TEMP_REG2))
                        .append("]");
                // move rid2 to tr2
                sb.append(moveValue(rid2, TEMP_REG2));
                // move tr2 to rid2 and rid3
                sb.append("[-")
                        .append(move(rid2))
                        .append("+")
                        .append(move(rid3))
                        .append("+")
                        .append(move(TEMP_REG2))
                        .append("]");
                break;
            }
            case PRINT: {
                Token r = advance();
                int rid = rvalue(r.token);
                sb.append(move(rid))
                        .append(".");
                break;
            }
            case INPUT: {
                Token r = advance();
                int rid = rvalue(r.token);
                sb.append(move(rid))
                        .append(",");
                break;
            }
            case SUB: {
                Token r1 = advance();
                consumeComma();
                Token r2 = advance();
                consumeComma();
                Token r3 = advance();
                int rid1 = rvalue(r1.token);
                int rid2 = rvalue(r2.token);
                int rid3 = rvalue(r3.token);
                // avoid adding duplicates
                if (rid1 == rid3) {
                    sb.append(move(rid2))
                            .append("[-")
                            .append(move(rid3))
                            .append("-")
                            .append(move(rid2))
                            .append("]")
                            .append(move(rid3));
                    break;
                }
                if (rid2 == rid3) {
                    sb.append(move(rid1))
                            .append("[-")
                            .append(move(rid3))
                            .append("-")
                            .append(move(rid1))
                            .append("]")
                            .append(move(rid3));
                    break;
                }
                // move rid1 to tr2
                sb.append(moveValue(rid1, TEMP_REG2));
                // clear rid3
                sb.append(move(rid3))
                        .append("[-]")
                        .append(move(TEMP_REG2));
                // move tr2 to rid1 and rid3
                sb.append("[-")
                        .append(move(rid1))
                        .append("+")
                        .append(move(rid3))
                        .append("+")
                        .append(move(TEMP_REG2))
                        .append("]");
                // move rid2 to tr2
                sb.append(moveValue(rid2, TEMP_REG2));
                // move tr2 to rid2 and -rid3
                sb.append("[-")
                        .append(move(rid2))
                        .append("+")
                        .append(move(rid3))
                        .append("-")
                        .append(move(TEMP_REG2))
                        .append("]");
                break;
            }
            case CLEAR: {
                int rid = rvalue(advance().token);
                sb.append(move(rid))
                        .append("[-]");
                break;
            }
            case COPY: {
                int rid1 = rvalue(advance().token);
                consumeComma();
                int rid2 = rvalue(advance().token);
                if (rid1 == rid2)
                    break;
                sb.append(move(rid2)).append("[-]");
                // move rid1 to tr2
                sb.append(moveValue(rid1, TEMP_REG2));
                // move cr to rid1 and rid2
                sb.append("[-")
                        .append(move(rid1))
                        .append("+")
                        .append(move(rid2))
                        .append("+")
                        .append(move(TEMP_REG2))
                        .append("]");
                break;
            }
            case SWAP: {
                int rid1 = rvalue(advance().token);
                consumeComma();
                int rid2 = rvalue(advance().token);
                if (rid1 == rid2)
                    break;
                sb.append(moveValue(rid1, TEMP_REG2));
                sb.append(moveValue(rid2, rid1));
                sb.append(moveValue(TEMP_REG2, rid2));
                break;
            }
            case PRINTC: {
                Token literal = advance();
                if (literal.type != TokenType.CHAR_CONST && literal.type != TokenType.LITERAL) throw new CompilerException("Expected char literal.", literal);
                char c;
                if (literal.type == TokenType.CHAR_CONST) c = literal.token.charAt(1);
                else c = (char) Integer.parseInt(literal.token);
                sb.append(move(TEMP_REG2))
                        .append("+".repeat(c))
                        .append(".")
                        .append("[-]");
                break;
            }
            case PRINTSTR: {
                Token literal = advance();
                if (literal.type != TokenType.STRING_CONST) throw new CompilerException("Expected string.", literal);
                String str = literal.token.substring(1, literal.token.length()-1);
                int s0 = 0;
                sb.append(move(TEMP_REG2));
                for (char c : str.toCharArray()) {
                    if (c == ' ') {
                        sb.append(move(SPACE_REG))
                                .append(".");
                        sb.append(move(TEMP_REG2));
                        continue;
                    }
                    if (c > s0) {
                        sb.append("+".repeat(c - s0));
                    } else {
                        sb.append("-".repeat(s0 - c));
                    }
                    sb.append(".");
                    s0 = c;
                }
                sb.append("[-]");
                break;
            }
            case WHILE: {
                Token returnLocation = advance();
                consume(TokenType.LBRACE, "Expected '{'");
                sb.append(move(rvalue(returnLocation.token))).append("[");
                while (!match(TokenType.RBRACE)) {
                    sb.append(compileStatement());
                }
                sb.append(move(rvalue(returnLocation.token))).append("]");
                break;
            }
            //TODO: add this instruction
            //VPRINT_U: prints the current value as an unsigned integer
            case VPRINT_U: {
                throw new CompilerException("Unsupported instruction.", current);
            }
            case PRINTLN: {
                sb.append(move(TEMP_REG4))
                        .append("++++++++++.[-]");
                break;
            }
            case MUL: {
                int rid1 = rvalue(advance().token);
                consumeComma();
                int rid2 = rvalue(advance().token);
                consumeComma();
                int rid3 = rvalue(advance().token);

                sb.append(moveValue(rid1, TEMP_REG1));
                sb.append(move(TEMP_REG1));
                sb.append("[-");
                sb.append(move(TEMP_REG2));
                sb.append("+");
                sb.append(move(rid1));
                sb.append("+");
                sb.append(move(TEMP_REG1));
                sb.append("]");
                sb.append(moveValue(TEMP_REG2, TEMP_REG1));
                sb.append(moveValue(rid2, TEMP_REG2));
                sb.append(move(TEMP_REG2));
                sb.append("[-");
                sb.append(move(TEMP_REG3));
                sb.append("+");
                sb.append(move(rid2));
                sb.append("+");
                sb.append(move(TEMP_REG2));
                sb.append("]");
                sb.append(moveValue(TEMP_REG3, TEMP_REG2));
                sb.append(move(rid3)).append("[-]");

                sb.append(move(TEMP_REG1)).append("[");

                sb.append(moveValue(TEMP_REG2, TEMP_REG3));
                sb.append("[-")
                        .append(move(TEMP_REG2)).append("+")
                        .append(move(rid3)).append("+")
                        .append(move(TEMP_REG3)).append("]");

                // Decrement TEMP_REG1
                sb.append(move(TEMP_REG1)).append("-]");
                sb.append(move(TEMP_REG2)).append("[-]");
                break;
            }
            case EQ: {
                //R1 -> T1; R2 -> T2
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                consumeComma();
                int rreg = rvalue(advance().token);
                sb.append(move(TEMP_REG1))
                        .append("[-]");
                sb.append(move(rreg))
                        .append("[-]");
                sb.append(move(TEMP_REG2))
                        .append("[-]");
                sb.append(move(TEMP_REG3))
                        .append("[-]");
                sb.append(move(TEMP_REG4))
                        .append("[-]");
                sb.append(move(TEMP_REG5))
                        .append("[-]");
                sb.append(moveValue(r1, TEMP_REG2));
                sb.append(move(TEMP_REG2))
                        .append("[-")
                        .append(move(r1))
                        .append("+")
                        .append(move(TEMP_REG1))
                        .append("+")
                        .append(move(TEMP_REG2))
                        .append("]");
                sb.append(moveValue(r2, TEMP_REG3));
                sb.append(move(TEMP_REG3))
                        .append("[-")
                        .append(move(r2))
                        .append("+")
                        .append(move(TEMP_REG2))
                        .append("+")
                        .append(move(TEMP_REG3))
                        .append("]");
                // ALGORITHM:
                // x[-y-x]+y[x-y[-]]
                sb.append(move(TEMP_REG1))
                        .append("[-")
                        .append(move(TEMP_REG2))
                        .append("-")
                        .append(move(TEMP_REG1))
                        .append("]+")
                        .append(move(TEMP_REG2))
                        .append("[")
                        .append(move(TEMP_REG1))
                        .append("-")
                        .append(move(TEMP_REG2))
                        .append("[-]]");
                sb.append(moveValue(TEMP_REG1, rreg));
                sb.append(move(TEMP_REG2))
                        .append("[-]")
                        .append(move(TEMP_REG1))
                        .append("[-]");
                break;
            }
            case TokenType.INV: {
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                //COPY R1 TO TEMP4
                sb.append(move(r1))
                        .append("[-")
                        .append(move(TEMP_REG4))
                        .append("+")
                        .append(move(TEMP_REG3))
                        .append("+")
                        .append(move(r1))
                        .append("]");
                sb.append(move(TEMP_REG3))
                        .append("[-")
                        .append(move(r1))
                        .append("+")
                        .append(move(TEMP_REG3))
                        .append("]");
                //PUT 1, R2
                sb.append(move(r2))
                        .append("[-]+");
                //IF TEMP4: CLEAR R2
                sb.append(move(TEMP_REG4))
                        .append("[")
                        .append(move(r2))
                        .append("-")
                        .append(move(TEMP_REG4))
                        .append("[-]]");
                break;
            }
            case NAT: {
                Token literal = advance();
                if (literal.type != TokenType.STRING_CONST) throw new CompilerException("Expected string.", literal);
                String str = literal.token.substring(1, literal.token.length()-1);
                sb.append(str);
                break;
            }
            case DEF: {
                Token literal1 = advance();
                if (literal1.type != TokenType.OTHER) throw new CompilerException("Expected MACRO.", literal1);
                String m1 = literal1.token;
                Token literal2 = advance();
                if (literal2.type != TokenType.STRING_CONST) throw new CompilerException("Expected string.", literal2);
                String str2 = literal2.token.substring(1, literal2.token.length()-1);
                macros.put(m1, str2);
                break;
            }
            case UNDEF: {
                Token literal1 = advance();
                if (literal1.type != TokenType.OTHER) throw new CompilerException("Expected MACRO.", literal1);
                String m1 = literal1.token;
                if (!macros.containsKey(m1))
                    throw new CompilerException("INVALID MACRO", current);
                macros.remove(m1);
                break;
            }
            case OTHER: {
                String macro = current.token;
                String text = macros.get(macro);
                if (text == null) {
                    text = cmacros.get(macro);
                    if (text == null)
                        throw new CompilerException("INVALID MACRO", current);
                    ArrayList<Token> tokens = new Scanner(text).scan();
                    Compiler c = new Compiler();
                    c.move(this.rpos);
                    sb.append(c.compile(tokens));
                    this.move(c.rpos);
                    break;
                }
                sb.append(text);
                break;
            }
            case IFDEF: {
                String macro = advance().token;
                consume(TokenType.LBRACE, "Expected '{'");
                boolean b = macros.containsKey(macro);
                while (!match(TokenType.RBRACE)) {
                    String s = compileStatement();
                    if (b)
                        sb.append(s).append("\n\t");
                }
                break;
            }
            case RDEF: {
                String macro = advance().token;
                consumeComma();
                int r = rvalue(advance().token);
                regdefs.put(macro, r);
                break;
            }
            case UNRDEF: {
                String macro = advance().token;
                regdefs.remove(macro);
                break;
            }
            case IFRDEF: {
                String macro = advance().token;
                consume(TokenType.LBRACE, "Expected '{'");
                boolean b = regdefs.containsKey(macro);
                while (!match(TokenType.RBRACE)) {
                    String s = compileStatement();
                    if (b)
                        sb.append(s).append("\n\t");
                }
                break;
            }
            case CDEF: {
                Token literal1 = advance();
                if (literal1.type != TokenType.OTHER) throw new CompilerException("Expected MACRO.", literal1);
                String m1 = literal1.token;
                consumeComma();
                consume(TokenType.LBRACE, "Expected '{'.");
                StringBuilder str1 = new StringBuilder();
                int depth = 0;
                while (true) {
                    Token t = advance();
                    if (t.type == TokenType.LBRACE)
                        depth++;
                    else if (t.type == TokenType.RBRACE)
                        if (depth == 0)
                            break;
                        else
                            depth--;
                    String s = t.token;
                    str1.append(s).append(' ');
                }
                cmacros.put(m1, str1.toString());
                break;
            }
            case UNCDEF: {
                Token literal1 = advance();
                if (literal1.type != TokenType.OTHER) throw new CompilerException("Expected MACRO.", literal1);
                String m1 = literal1.token;
                cmacros.remove(m1);
                break;
            }
            case IFCDEF: {
                String macro = advance().token;
                consume(TokenType.LBRACE, "Expected '{'");
                boolean b = cmacros.containsKey(macro);
                while (!match(TokenType.RBRACE)) {
                    String s = compileStatement();
                    if (b)
                        sb.append(s).append("\n\t");
                }
                break;
            }
            case IMPORT: {
                Token t = advance();
                String str = t.token;
                String file = str.substring(1, str.length()-1);
                try (FileInputStream stream = new FileInputStream(file)) {
                    byte[] text = stream.readAllBytes();
                    ArrayList<Token> tokens = new Scanner(new String(text)).scan();
                    sb.append(new Compiler().compile(tokens));
                } catch (FileNotFoundException e) {
                    throw new CompilerException("IMPORT not found.", t);
                } catch (IOException e) {
                    throw new CompilerException("Broken IMPORT.", t);
                }
                break;
            }
            case DIVMOD: {
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                consumeComma();
                int div = rvalue(advance().token);
                consumeComma();
                int mod = rvalue(advance().token);
                sb.append(move(div))
                        .append("[-]")
                        .append(move(mod))
                        .append("[-]");
                //COPY R1, R2 TO T1, T2
                sb.append(move(r1))
                        .append("[-")
                        .append(move(TEMP_REG1))
                        .append("+")
                        .append(move(TEMP_REG2))
                        .append("+")
                        .append(move(r1))
                        .append("]");
                sb.append(move(TEMP_REG2))
                        .append("[-")
                        .append(move(r1))
                        .append("+")
                        .append(move(TEMP_REG2))
                        .append("]");

                sb.append(move(r2))
                        .append("[-")
                        .append(move(TEMP_REG3))
                        .append("+")
                        .append(move(TEMP_REG2))
                        .append("+")
                        .append(move(r2))
                        .append("]");
                sb.append(move(TEMP_REG3))
                        .append("[-")
                        .append(move(r2))
                        .append("+")
                        .append(move(TEMP_REG3))
                        .append("]");
                //START ALGO
                sb.append(move(TEMP_REG1))
                        .append("[->[->+>>]>[<<+>>[-<+>]>+>>]<<<<<]\n" +
                                ">[>>>]>[[-<+>]>+>>]<<<<<")
                        .append(move(TEMP_REG2))
                        .append("[-]")
                        .append(move(TEMP_REG3))
                        .append("[-")
                        .append(move(mod))
                        .append("+")
                        .append(move(TEMP_REG3))
                        .append("]")
                        .append(move(TEMP_REG4))
                        .append("[-")
                        .append(move(div))
                        .append("+")
                        .append(move(TEMP_REG4))
                        .append("]");
                break;
            }
            case MOVE: {
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                if (r1 == r2)
                    break;
                sb.append(move(r1))
                        .append("[-")
                        .append(move(r2))
                        .append("+")
                        .append(move(r1))
                        .append("]");
                break;
            }
            default:
                throw new CompilerException("Unknown statement.", current);
        }
        consume(TokenType.SEMICOLON, "Expected semicolon after statement.");
        return sb.toString();
    }
    public boolean match(TokenType type) {
        if (tokens.get(position).type == type) {
            position++;
            return true;
        } return false;
    }
    public String moveValue(int r1, int r2) {
        return move(r1) +
                "[-" +
                move(r2) +
                "+" +
                move(r1) +
                "]" +
                move(r2);
    }
    public void consumeComma() {
        consume(TokenType.COMMA, "Expected comma.");
    }
    public Token advance() {
        if (isAtEnd()) {
            throw new CompilerException("Unexpected end of input.", tokens.getLast());
        }
        return tokens.get(position++);
    }
    public boolean isAtEnd() {
        return tokens.get(position).type == TokenType.EOF;
    }
    public void consume(TokenType type, String message) {
        Token ad = advance();
        if (ad.type != type) throw new CompilerException(message, ad);
    }
    public int rvalue(String rname) {
        if (regdefs.containsKey(rname)) {
            return regdefs.get(rname);
        }
        if (!rname.startsWith("r")) {
            throw new IllegalArgumentException("Invalid register name: " + rname);
        }
        return Integer.parseInt(rname.substring(1)) + NUM_SPECIAL_REGS;
    }
    public String move(int currentR) {
        String s = "";
        if (currentR > rpos) {
            s = ">".repeat(currentR-rpos);
        } else if (currentR < rpos) {
            s = "<".repeat(rpos-currentR);
        }
        rpos = currentR;
        return s;
    }
}
