package compilation.bf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static compilation.bf.TokenType.*;

public class BytecodeCompiler {
    // number represents the number of bytes per operand (goes until 2^24 registers)
    private static final byte   OP_ADD1 = 0,
            OP_ADD2 = 1,
            OP_ADD3 = 2,
            OP_SUB1 = 3,
            OP_SUB2 = 4,
            OP_SUB3 = 5,
            OP_CLEAR1 = 6,
            OP_CLEAR2 = 7,
            OP_CLEAR3 = 8,
            OP_COPY1 = 9,
            OP_COPY2 = 10,
            OP_COPY3 = 11,
            OP_PRINT1 = 12,
            OP_PRINT2 = 13,
            OP_PRINT3 = 14,
            OP_INPUT1 = 15,
            OP_INPUT2 = 16,
            OP_INPUT3 = 17,
            OP_SWAP1 = 18,
            OP_SWAP2 = 19,
            OP_SWAP3 = 20,
            OP_PRINTC = 21,
            OP_PRINTSTR = 22,
            OP_ENDB = 23,
            OP_INC1 = 24,
            OP_INC2 = 25,
            OP_INC3 = 26,
            OP_DEC1 = 27,
            OP_DEC2 = 28,
            OP_DEC3 = 29,
            OP_JIF1 = 30,
            OP_JIF2 = 31,
            OP_JIF3 = 32,
            OP_JMP1 = 33,
            OP_JMP2 = 34,
            OP_JMP3 = 35,
            OP_MUL1 = 36,
            OP_MUL2 = 37,
            OP_MUL3 = 38,
            OP_EQ1 = 39,
            OP_EQ2 = 40,
            OP_EQ3 = 41,
            OP_INV1 = 42,
            OP_INV2 = 43,
            OP_INV3 = 44,
            OP_PLN = 45,
            OP_NAT = 46,
            OP_DM1 = 47,
            OP_DM2 = 48,
            OP_DM3 = 49,
            OP_MOV1 = 50,
            OP_MOV2 = 51,
            OP_MOV3 = 52,
            OP_PUT1 = 53,
            OP_PUT2 = 54,
            OP_PUT3 = 55;

    private static final HashMap<String, String> macros = new HashMap<>(),
            cmacros = new HashMap<>();
    private static final HashMap<String, Integer>   regdefs = new HashMap<>();
    private ArrayList<Byte> codes;
    ArrayList<Token> tokens;
    int pointer = 0;
    public ArrayList<Byte> compile(ArrayList<Token> tokens) {
        this.tokens = tokens;
        codes = new ArrayList<>();
        while (!isAtEnd()) {
            if (tokens.get(pointer).type == TokenType.EOF) break;
            codes.addAll(compileStatement());
        }
        return codes;
    }
    public boolean isAtEnd() {
        return pointer >= tokens.size();
    }
    public ArrayList<Byte> compileStatement() {
        Token current = advance();
        ArrayList<Byte> bytes = new ArrayList<>();
        switch(current.type) {
            case DEF -> {
                Token literal1 = advance();
                if (literal1.type != TokenType.OTHER) throw new CompilerException("Expected MACRO.", literal1);
                String m1 = literal1.token;
                Token literal2 = advance();
                if (literal2.type != TokenType.STRING_CONST) throw new CompilerException("Expected string.", literal2);
                String str2 = literal2.token.substring(1, literal2.token.length()-1);
                macros.put(m1, str2);
            }
            case UNDEF -> {
                Token literal1 = advance();
                if (literal1.type != TokenType.OTHER) throw new CompilerException("Expected MACRO.", literal1);
                String m1 = literal1.token;
                if (!macros.containsKey(m1))
                    throw new CompilerException("INVALID MACRO", current);
                macros.remove(m1);
            }
            case OTHER -> {
                String macro = current.token;
                String text = macros.get(macro);
                if (text == null) {
                    text = cmacros.get(macro);
                    if (text == null)
                        throw new CompilerException("INVALID MACRO", current);
                    ArrayList<Token> tokens = new Scanner(text).scan();
                    BytecodeCompiler c = new BytecodeCompiler();
                    tokens.removeLast();
                    bytes.addAll(c.compile(tokens));
                    break;
                }
                bytes.add(OP_NAT);
                char[] c = text.toCharArray();
                Byte[] b = new Byte[c.length];
                for (int i = 0; i < c.length; i++) {
                    b[i] = (byte) c[i];
                }
                bytes.addAll(new ArrayList<>(List.of(b)));
                bytes.add(OP_ENDB);
            }
            case IFDEF -> {
                String macro = advance().token;
                consume(LBRACE, "Expected '{'");
                boolean b = macros.containsKey(macro);
                while (!match(TokenType.RBRACE)) {
                    ArrayList<Byte> s = compileStatement();
                    if (b)
                        bytes.addAll(s);
                }
            }
            case RDEF -> {
                String macro = advance().token;
                consumeComma();
                int r = rvalue(advance().token);
                regdefs.put(macro, r);
            }
            case UNRDEF -> {
                String macro = advance().token;
                regdefs.remove(macro);
            }
            case IFRDEF -> {
                String macro = advance().token;
                consume(LBRACE, "Expected '{'");
                boolean b = regdefs.containsKey(macro);
                while (!match(TokenType.RBRACE)) {
                    ArrayList<Byte> s = compileStatement();
                    if (b)
                        bytes.addAll(s);
                }
            }
            case CDEF -> {
                Token literal1 = advance();
                if (literal1.type != TokenType.OTHER) throw new CompilerException("Expected MACRO.", literal1);
                String m1 = literal1.token;
                consumeComma();
                consume(LBRACE, "Expected '{'.");
                StringBuilder str1 = new StringBuilder();
                int depth = 0;
                while (true) {
                    Token t = advance();
                    if (t.type == LBRACE)
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
            }
            case UNCDEF -> {
                Token literal1 = advance();
                if (literal1.type != TokenType.OTHER) throw new CompilerException("Expected MACRO.", literal1);
                String m1 = literal1.token;
                cmacros.remove(m1);
            }
            case IFCDEF -> {
                String macro = advance().token;
                consume(LBRACE, "Expected '{'");
                boolean b = cmacros.containsKey(macro);
                while (!match(TokenType.RBRACE)) {
                    ArrayList<Byte> s = compileStatement();
                    if (b)
                        bytes.addAll(s);
                }
            }
            case IMPORT -> {
                Token t = advance();
                String str = t.token;
                String file = str.substring(1, str.length()-1);
                try (FileInputStream stream = new FileInputStream(file)) {
                    byte[] text = stream.readAllBytes();
                    ArrayList<Token> tokens = new Scanner(new String(text)).scan();
                    bytes.addAll(new BytecodeCompiler().compile(tokens));
                } catch (FileNotFoundException e) {
                    throw new CompilerException("IMPORT not found.", t);
                } catch (IOException e) {
                    throw new CompilerException("Broken IMPORT.", t);
                }
            }
            case ADD -> {
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                consumeComma();
                int r3 = rvalue(advance().token);
                buildInstruction(bytes, OP_ADD1, OP_ADD2, OP_ADD3, r1, r2, r3);
            }
            case SUB -> {
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                consumeComma();
                int r3 = rvalue(advance().token);
                buildInstruction(bytes, OP_SUB1, OP_SUB2, OP_SUB3, r1, r2, r3);
            }
            case CLEAR -> {
                int r = rvalue(advance().token);
                buildInstruction(bytes, OP_CLEAR1, OP_CLEAR2, OP_CLEAR3, r);
            }
            case COPY -> {
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                buildInstruction(bytes, OP_COPY1, OP_COPY2, OP_COPY3, r1, r2);
            }
            case PRINT -> {
                int r = rvalue(advance().token);
                buildInstruction(bytes, OP_PRINT1, OP_PRINT2, OP_PRINT3, r);
            }
            case INPUT -> {
                int r = rvalue(advance().token);
                buildInstruction(bytes, OP_INPUT1, OP_INPUT2, OP_INPUT3, r);
            }
            case SWAP -> {
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                buildInstruction(bytes, OP_SWAP1, OP_SWAP2, OP_SWAP3, r1, r2);
            }
            case MOVE -> {
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                buildInstruction(bytes, OP_MOV1, OP_MOV2, OP_MOV3, r1, r2);
            }
            case PRINTC -> {
                char c = advance().token.charAt(1);
                bytes.add(OP_PRINTC);
                bytes.add((byte) c);
            }
            case PRINTSTR -> {
                Token t = consume(STRING_CONST, "Invalid string");
                String str = t.token.substring(1, t.token.length()-1);
                bytes.add(OP_PRINTSTR);
                char[] c = str.toCharArray();
                Byte[] b = new Byte[c.length];
                for (int i = 0; i < c.length; i++) {
                    b[i] = (byte) c[i];
                }
                bytes.addAll(new ArrayList<>(List.of(b)));
                bytes.add(OP_ENDB);
            }
            case WHILE -> {
                int r = rvalue(advance().token);
                ArrayList<Byte> s = new ArrayList<>();
                consume(LBRACE, "Expected '{'.");
                while (!match(RBRACE)) {
                    s.addAll(compileStatement());
                }
                // jump ahead
                if (s.size() <= 251 && r <= 255)
                    buildInstruction(bytes, OP_JIF1, OP_JIF2, OP_JIF3, r, s.size()+2);
                else if (s.size() <= 0xFFF8 && r <= 0xFFFF)
                    buildInstruction(bytes, OP_JIF1, OP_JIF2, OP_JIF3, r, s.size()+3);
                else
                    buildInstruction(bytes, OP_JIF1, OP_JIF2, OP_JIF3, r, s.size()+4);

                bytes.addAll(s);
                int cur = bytes.size();
                // jump back
                if (cur <= 255)
                    buildInstruction(bytes, OP_JMP1, OP_JMP2, OP_JMP3, cur +2);
                else if (s.size() <= 0xFFFF)
                    buildInstruction(bytes, OP_JMP1, OP_JMP2, OP_JMP3, cur +3);
                else
                    buildInstruction(bytes, OP_JMP1, OP_JMP2, OP_JMP3, cur +4);
            }
            case DIVMOD -> {
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                consumeComma();
                int r3 = rvalue(advance().token);
                consumeComma();
                int r4 = rvalue(advance().token);
                buildInstruction(bytes, OP_DM1, OP_DM2, OP_DM3, r1, r2, r3, r4);
            }
            case PRINTLN -> bytes.add(OP_PLN);
            case INC -> {
                int r1 = rvalue(advance().token);
                consumeComma();
                int i = Integer.parseInt(advance().token);
                buildInstruction(bytes, OP_INC1, OP_INC2, OP_INC3, r1);
                bytes.add((byte) i);
            }
            case DEC -> {
                int r1 = rvalue(advance().token);
                consumeComma();
                int i = Integer.parseInt(advance().token);
                buildInstruction(bytes, OP_DEC1, OP_DEC2, OP_DEC3, r1);
                bytes.add((byte) i);
            }
            case INV -> {
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                buildInstruction(bytes, OP_INV1, OP_INV2, OP_INV3, r1, r2);
            }
            case MUL -> {
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                consumeComma();
                int r3 = rvalue(advance().token);
                buildInstruction(bytes, OP_MUL1, OP_MUL2, OP_MUL3, r1, r2, r3);
            }
            case EQ -> {
                int r1 = rvalue(advance().token);
                consumeComma();
                int r2 = rvalue(advance().token);
                consumeComma();
                int r3 = rvalue(advance().token);
                buildInstruction(bytes, OP_EQ1, OP_EQ2, OP_EQ3, r1, r2, r3);
            }
            case PUT -> {
                int i = Integer.parseInt(advance().token);
                consumeComma();
                int r = rvalue(advance().token);
                buildInstruction(bytes, OP_PUT1, OP_PUT2, OP_PUT3, r);
                bytes.add((byte) i);
            }
            case NAT -> {
                Token t = consume(STRING_CONST, "Expected string");
                String str = t.token.substring(1, t.token.length()-1);
                bytes.add(OP_NAT);
                char[] c = str.toCharArray();
                Byte[] b = new Byte[c.length];
                for (int i = 0; i < c.length; i++) {
                    b[i] = (byte) c[i];
                }
                bytes.addAll(new ArrayList<>(List.of(b)));
                bytes.add(OP_ENDB);
            }
            default -> throw new CompilerException("Invalid statement.", current);
        }
        consume(SEMICOLON, "Expected ';'.");
        return bytes;
    }
    public Token advance() {
        return tokens.get(pointer++);
    }
    public Token consume(TokenType type, String message) {
        Token ad = advance();
        if (ad.type != type) throw new CompilerException(message, ad);
        return ad;
    }
    public int rvalue(String rname) {
        if (regdefs.containsKey(rname)) {
            return regdefs.get(rname);
        }
        if (!rname.startsWith("r")) {
            throw new IllegalArgumentException("Invalid register name: " + rname);
        }
        return Integer.parseInt(rname.substring(1));
    }
    public boolean match(TokenType type) {
        if (tokens.get(pointer).type == type) {
            pointer++;
            return true;
        } return false;
    }
    public void consumeComma() {
        consume(TokenType.COMMA, "Expected comma.");
    }
    public void buildInstruction(ArrayList<Byte> bytes, byte op1, byte op2, byte op3, int... operands) {
        int size = 1;
        for (int operand : operands) {
            if (operand < 0) throw new IllegalArgumentException("Negative operand: " + operand);
            if (operand >= 256 && size < 2)
                size = 2;
            else if (operand >= 256*256 && size < 3)
                size = 3;
        }
        bytes.add(size == 1 ? op1 : (size == 2 ? op2 : op3));
        for (int operand : operands) {
            int b1 = (operand >> 16) & 0xFF;
            int b2 = (operand >> 8) & 0xFF;
            int b3 = operand & 0xFF;
            if (size == 3) {
                bytes.add((byte) b1);
                bytes.add((byte) b2);
                bytes.add((byte) b3);
            } else if (size == 2) {
                bytes.add((byte) b2);
                bytes.add((byte) b3);
            } else
                bytes.add((byte) b3);
        }
    }
}
