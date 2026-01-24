package compilation.ir;

import compilation.ir.instructions.*;
import compilation.ir.instructions.cf.FunctionDefinitionInstruction;
import compilation.ir.instructions.cf.IfInstruction;
import compilation.ir.instructions.cf.WhileInstruction;
import compilation.ir.instructions.math.*;
import compilation.ir.instructions.math.num.*;
import compilation.ir.instructions.mem.CopyInstruction;
import expressions.*;
import main.*;
import statements.*;

import java.util.ArrayList;
import java.util.List;

class CompilerException extends RuntimeException {
    public CompilerException(String s) {
        super(s);
    }
}

public class Compiler implements StatementVisitor<ArrayList<Instruction>>, ExpressionVisitor<ExpressionResult> {
    private Scope scope = new Scope(null);

    public ArrayList<Instruction> compile(List<Statement> ast) {
        ArrayList<Instruction> out = new ArrayList<>();
        for (Statement stmt : ast) {
            out.addAll(stmt.accept(this));
        }
        return out;
    }
    private ArrayList<Instruction> freeScope() {
        ArrayList<Instruction> l = scope.freeAll();
        scope = scope.parent;
        return l;
    }
    private void deepenScope() {
        scope = new Scope(scope);
    }
    private void functionScope(UniqueFunction fn) {
        scope = new FunctionScope(scope, fn);
    }
    public VariableType getType(String typeName) {
        if ("byte".equals(typeName))
            return ByteType.INSTANCE;
        else if ("bool".equals(typeName))
            return BoolType.INSTANCE;
        else
            return scope.retrieveStruct(typeName);
    }
    @Override
    public ArrayList<Instruction> visitExpressionStatement(ExpressionStatement stmt) {
        ExpressionResult r = stmt.expr.accept(this);
        return r.instructions();
    }
    public ArrayList<Instruction> visitPrintStatement(PrintStatement stmt) {
        ExpressionResult r = stmt.expr.accept(this);
        ArrayList<Instruction> instructions = new ArrayList<>(r.instructions());
        instructions.add(new PrintInstruction(r.result()));
        return instructions;
    }
    public ArrayList<Instruction> visitVarStatement(VarStatement stmt) {
        String name = stmt.var;
        ExpressionResult res = stmt.assignment.accept(this);
        VariableType type = getType(stmt.type);
        if (!res.result().type.equals(type)) {
            throw new CompilerException("Type mismatch. No further information available.");
        }
        ArrayList<Instruction> instructions = new ArrayList<>(res.instructions());
        instructions.add(scope.register(new CompiledVariable(name, type)));
        instructions.add(new CopyInstruction(res.result(), scope.retrieveVar(name)));
        return instructions;
    }
    public ArrayList<Instruction> visitBlockStatement(BlockStatement stmt) {
        deepenScope();
        ArrayList<Instruction> out = new ArrayList<>();
        for (Statement s : stmt.statements)
            out.addAll(s.accept(this));
        out.addAll(freeScope());
        return out;
    }
    public ArrayList<Instruction> visitWhileStatement(WhileStatement stmt) {
        ExpressionResult condition = stmt.expr.accept(this);
        ArrayList<Instruction> output = new ArrayList<>(condition.instructions());
        UniqueVariable loopVariable = condition.result();
        deepenScope();
        ArrayList<Instruction> body = stmt.stmt.accept(this);
        ExpressionResult condition2 = stmt.expr.accept(this);
        body.addAll(condition2.instructions());
        body.add(new CopyInstruction(condition2.result(), loopVariable));
        output.add(new WhileInstruction(body, loopVariable));
        output.addAll(freeScope());
        return output;
    }
    public ArrayList<Instruction> visitIfStatement(IfStatement stmt) {
        ArrayList<Instruction> elseBranch = null;
        if (stmt.hasElseStatement())
            elseBranch = new ArrayList<>();
        ExpressionResult condition = stmt.expr.accept(this);
        if (!(condition.result().type instanceof BoolType))
            throw new CompilerException("Invalid if statement");
        ArrayList<Instruction> out = new ArrayList<>(condition.instructions());
        TempAllocationResult tmp = scope.allocTemp(BoolType.INSTANCE);
        out.add(tmp.instr());
        UniqueVariable uv = scope.retrieveVar(tmp.tempName());
        UniqueVariable variable = condition.result();
        out.add(new CopyInstruction(variable, uv));
        deepenScope();
        ArrayList<Instruction> thenBranch = new ArrayList<>(stmt.thenBranch.accept(this));
        thenBranch.addAll(freeScope());
        if (stmt.hasElseStatement()) {
            deepenScope();
            elseBranch.addAll(stmt.elseBranch.accept(this));
            elseBranch.addAll(freeScope());
        }
        out.add(new IfInstruction(uv, thenBranch, elseBranch));
        return out;
    }
    public ArrayList<Instruction> visitNativeStatement(NativeStatement stmt) {
        ArrayList<Instruction> out = new ArrayList<>();
        out.add(new NativeInstruction(stmt.code));
        return out;
    }
    public ArrayList<Instruction> visitFunctionStatement(FunctionStatement stmt) {
        ArrayList<Instruction> out = new ArrayList<>();
        String type = stmt.returnType;
        VariableType returnType = getType(type);
        ArrayList<String> types = new ArrayList<>();
        for (VariableSymbol v : stmt.args) {
            types.add(getType(v.type).toString());
        }
        String fname = getFunctionUniqueName(stmt.name, types);
        String rname = fname+"__return";
        CompiledVariable crv = new CompiledVariable(rname, returnType);
        out.add(scope.register(crv));
        UniqueVariable returnVar = scope.retrieveVar(rname);
        ArrayList<FunctionArgument> args = new ArrayList<>();
        int counter = 0;
        for (VariableSymbol v : stmt.args) {
            String rn = fname+"__input"+counter++;
            CompiledVariable cv = new CompiledVariable(rn, getType(v.type));
            out.add(scope.register(cv));
            args.add(new FunctionArgument(v.name, scope.retrieveVar(rn)));
        }
        UniqueFunction fn = new UniqueFunction(stmt.name, args, returnVar);
        scope.register(fn);
        functionScope(fn);
        ArrayList<Instruction> body = new ArrayList<>();
        for (Statement fnstmt : stmt.body) {
            body.addAll(fnstmt.accept(this));
        }
        body.addAll(freeScope());
        out.add(new FunctionDefinitionInstruction(fn.getUniqueName(), body));
        return out;
    }
    public static String getFunctionUniqueName(String fname, ArrayList<String> types) {
        StringBuilder sb = new StringBuilder(fname).append("_");
        for (String s : types) {
            sb.append(s).append("_");
        }
        return sb.toString();
    }
    public ArrayList<Instruction> visitStructStatement(StructStatement stmt) {
        ArrayList<StructField> fields = new ArrayList<>();
        for (Variable v : stmt.variables) {
            for (StructField field : fields)
                if (field.name().equals(v.name()))
                    throw new CompilerException("Duplicate fields");
            fields.add(new StructField(v.name(), getType(v.type())));
        }
        StructType type = new StructType(fields);
        scope.register(new CompiledStruct(stmt.name, type));
        return genConstructor(stmt, type);
    }
    public ArrayList<Instruction> genConstructor(StructStatement stmt, StructType type) {
        String name = "_"+stmt.name;
        String rname = name+"_result";
        Instruction instr = scope.register(new CompiledVariable(rname, type));
        UniqueFunction ufn = new UniqueFunction(name, new ArrayList<>(), scope.retrieveVar(rname));
        scope.register(ufn);
        ArrayList<Instruction> out = new ArrayList<>();
        out.add(instr);
        functionScope(ufn);
        ArrayList<Instruction> body = new ArrayList<>();
        out.addAll(freeScope());
        out.add(new FunctionDefinitionInstruction(scope.retrieveFunction(new FunctionRecord(name, new ArrayList<>())).getUniqueName(), body));
        return out;
    }
    public ArrayList<Instruction> visitReturnStatement(ReturnStatement stmt) {
        FunctionScope fnscope = findFunctionScope();
        if (fnscope == null)
            throw new CompilerException("return outside function");
        ExpressionResult expr = stmt.expr.accept(this);
        if (expr.result().type != fnscope.getReturnVar().type)
            throw new CompilerException("Invalid return type");
        ArrayList<Instruction> out = new ArrayList<>(expr.instructions());
        out.add(new CopyInstruction(expr.result(), fnscope.getReturnVar()));
        return out;
    }
    private FunctionScope findFunctionScope() {
        Scope s = scope;
        while (s != null) {
            if (s instanceof FunctionScope)
                return (FunctionScope) s;
            s = s.parent;
        }
        return null;
    }
    public ExpressionResult visitBinaryExpression(BinaryExpression expr) {
        ArrayList<Instruction> out = new ArrayList<>();
        ExpressionResult res1 = expr.left.accept(this);
        ExpressionResult res2 = expr.right.accept(this);
        checkTypeValidity(res1.result().type, res2.result().type, expr.operator.type);
        out.addAll(res1.instructions());
        out.addAll(res2.instructions());
        UniqueVariable result;
        if (expr.operator.type == TokenType.EQ) {
            if (!(expr.left instanceof IdentifierExpression || expr.left instanceof DotExpression))
                throw new CompilerException("Invalid assignment target");
            out.addAll(res2.instructions());
            out.add(new CopyInstruction(res2.result(), res1.result()));
            return new ExpressionResult(out, res1.result());
        }
        TempAllocationResult ta = scope.allocTemp(getBinaryReturnType(res1.result().type, res2.result().type, expr.operator.type));
        out.add(ta.instr());
        result = scope.retrieveVar(ta.tempName());
        switch (expr.operator.type) {
            case PLUS -> out.add(new AddInstruction(res1.result(), res2.result(), result));
            case MINUS -> out.add(new SubInstruction(res1.result(), res2.result(), result));
            case STAR -> out.add(new MulInstruction(res1.result(), res2.result(), result));
            case SLASH -> out.add(new DivInstruction(res1.result(), res2.result(), result));
            case MOD -> out.add(new ModInstruction(res1.result(), res2.result(), result));
            case EXPONENT -> out.add(new ExpInstruction(res1.result(), res2.result(), result));
            case EQEQ -> out.add(new EqInstruction(res1.result(), res2.result(), result));
            case NEQ -> {
                out.add(new EqInstruction(res1.result(), res2.result(), result));
                out.add(new InvInstruction(result, result));
            }
            case AND -> out.add(new AndInstruction(res1.result(), res2.result(), result));
            case OR -> out.add(new OrInstruction(res1.result(), res2.result(), result));
            case XOR -> out.add(new XorInstruction(res1.result(), res2.result(), result));
            case GT -> out.add(new GtInstruction(res1.result(), res2.result(), result));
            case GTEQ -> {
                out.add(new GtInstruction(res2.result(), res1.result(), result));
                out.add(new InvInstruction(result, result));
            }
            case LT -> out.add(new GtInstruction(res2.result(), res1.result(), result));
            case LTEQ -> {
                out.add(new GtInstruction(res1.result(), res2.result(), result));
                out.add(new InvInstruction(result, result));
            }
            default -> throw new CompilerException("Invalid BINARY operator");
        }
        return new ExpressionResult(out, result);
    }
    public void checkTypeValidity(VariableType t1, VariableType t2, TokenType op) {
        switch (op) {
            case PLUS, MINUS, STAR, SLASH, EXPONENT, MOD, GT, GTEQ, LT, LTEQ -> {
                if (!(t1 instanceof ByteType) || !(t2 instanceof ByteType))
                    throw new CompilerException("Invalid types");
            }
            case OR, AND, XOR -> {
                if (!(t1 instanceof BoolType) || !(t2 instanceof BoolType))
                    throw new CompilerException("Invalid types");
            }
            case EQ -> {
                if (!(t1.equals(t2)))
                    throw new CompilerException("Invalid types");
            }
            default -> {} // ALL TYPES VALID
        }
    }
    public VariableType getBinaryReturnType(VariableType t1, VariableType t2, TokenType op) {
        return switch(op) {
            case PLUS, MINUS, STAR, SLASH, EXPONENT, MOD -> ByteType.INSTANCE;
            case OR, AND, XOR, EQEQ, NEQ, GT, GTEQ, LT, LTEQ-> BoolType.INSTANCE;
            case EQ -> t1;
            default -> throw new CompilerException("Invalid BINARY operator");
        };
    }
    public ExpressionResult visitUnaryExpression(UnaryExpression expr) {
        ExpressionResult er = expr.expr.accept(this);
        ArrayList<Instruction> out = new ArrayList<>(er.instructions());
        checkTypeValidity(er.result().type, expr.operator.type);
        TempAllocationResult result = scope.allocTemp(getUnaryReturnType(expr.operator.type));
        out.add(result.instr());
        UniqueVariable v = scope.retrieveVar(result.tempName());
        switch(expr.operator.type) {
            case MINUS -> out.add(new NegInstruction(er.result(), v));
            case QUESTION -> out.add(new BoolInstruction(er.result(), v));
            case BANG -> out.add(new InvInstruction(er.result(), v));
            default -> throw new CompilerException("Invalid UNARY operator");
        }
        return new ExpressionResult(out, v);
    }
    public void checkTypeValidity(VariableType t, TokenType op) {
        switch (op) {
            case MINUS -> {
                if (!(t instanceof ByteType))
                    throw new CompilerException("Invalid type");
            }
            case QUESTION -> {
                if (t instanceof BoolType || t instanceof StructType)
                    throw new CompilerException("Invalid type");
            }
            case BANG -> {
                if (!(t instanceof BoolType))
                    throw new CompilerException("Invalid type");
            }
            default -> throw new CompilerException("Invalid operator");
        }
    }
    public VariableType getUnaryReturnType(TokenType op) {
        return switch(op) {
            case BANG, QUESTION -> BoolType.INSTANCE;
            case MINUS -> ByteType.INSTANCE;
            default -> throw new CompilerException("Invalid UNARY operator");
        };
    }
    public ArrayList<Instruction> visitPrintstrStatement(PrintstrStatement stmt) {
        ArrayList<Instruction> out = new ArrayList<>();
        out.add(new PrintstrInstruction(stmt.str));
        return out;
    }
    public ExpressionResult visitLiteralExpression(LiteralExpression expr) {
        Object obj = expr.value;
        ArrayList<Instruction> out = new ArrayList<>();
        byte b = getValue(obj);
        TempAllocationResult tmp = scope.allocTemp(getType(obj));
        String tname = tmp.tempName();
        out.add(tmp.instr());
        UniqueVariable uv = scope.retrieveVar(tname);
        out.add(new PutInstruction(uv, b));
        return new ExpressionResult(out, uv);
    }
    public byte getValue(Object o) {
        if (o instanceof Boolean b)
            return b ? (byte) 1 : 0;
        else if (o instanceof Integer i)
            if (i >= 256)
                throw new CompilerException("Invalid byte size");
            else
                return (byte) (int) i;
        else if (o instanceof Character c) {
            int i = (int) c;
            if (i >= 256)
                throw new CompilerException("Invalid character");
            return (byte) i;
        }
        throw new CompilerException("Invalid variable state");
    }
    public VariableType getType(Object o) {
        if (o instanceof Boolean)
            return BoolType.INSTANCE;
        else if (o instanceof Integer || o instanceof Character)
            return ByteType.INSTANCE;
        throw new CompilerException("Invalid variable state");
    }
    public ExpressionResult visitIdentifierExpression(IdentifierExpression expr) {
        UniqueVariable variable = scope.retrieveVar(expr.name);
        return new ExpressionResult(new ArrayList<>(), variable);
    }
    public ExpressionResult visitCallExpression(CallExpression expr) {
        ArrayList<Instruction> out = new ArrayList<>();
        String fname = expr.function;
        ArrayList<ExpressionResult> ers = new ArrayList<>();
        for (Expression e : expr.args) {
            ers.add(e.accept(this));
        }
        ArrayList<UniqueVariable> uvs = new ArrayList<>();
        for (ExpressionResult r : ers) {
            uvs.add(r.result());
            out.addAll(r.instructions());
        }
        ArrayList<VariableType> vts = new ArrayList<>();
        for (UniqueVariable r : uvs) {
            vts.add(r.type);
        }
        UniqueFunction fn = scope.retrieveFunction(new FunctionRecord(fname, vts));
        if (uvs.size() != fn.params().size())
            throw new CompilerException("Invalid function argument count");
        for (int i = 0; i < uvs.size(); i++) {
            out.add(new CopyInstruction(uvs.get(i), fn.params().get(i).position()));
        }
        out.add(new CallInstruction(fn.getUniqueName()));
        return new ExpressionResult(out, fn.result());
    }
    public ExpressionResult visitDotExpression(DotExpression expr) {
        ExpressionResult res = expr.left.accept(this);
        UniqueVariable uv = res.result();
        VariableType vtype = uv.type;
        if (!(vtype instanceof StructType type))
            throw new CompilerException("Invalid member expression");
        int i = type.getFieldOffset(expr.right);
        return new ExpressionResult(new ArrayList<>(), new MemberVariable(uv.name, uv.identifier, type.getType(i), i));
    }
}
