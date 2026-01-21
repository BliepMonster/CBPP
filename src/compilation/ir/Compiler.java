package compilation.ir;

import compilation.CompiledVariable;
import compilation.ir.instructions.*;
import expressions.*;
import main.ByteType;
import main.VariableType;
import statements.*;

import java.util.ArrayList;

class CompilerException extends RuntimeException {
    public CompilerException(String s) {
        super(s);
    }
}

public class Compiler implements StatementVisitor<ArrayList<Instruction>>, ExpressionVisitor<ExpressionResult> {
    private Scope scope = new Scope(null);
    private ArrayList<Instruction> freeScope() {
        ArrayList<Instruction> l = scope.freeAll();
        scope = scope.parent;
        return l;
    }
    private void deepenScope() {
        scope = new Scope(scope);
    }
    public VariableType getType(String typeName) {
        if ("byte".equals(typeName))
            return ByteType.INSTANCE;
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
        if (!res.result().type().equals(type)) {
            throw new CompilerException("Type mismatch. No further information available.");
        }
        ArrayList<Instruction> instructions = new ArrayList<>(res.instructions());
        instructions.add(scope.register(new CompiledVariable(name, type)));
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
        body.addAll(condition.instructions());
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
        ArrayList<Instruction> out = new ArrayList<>(condition.instructions());
        UniqueVariable variable = condition.result();
        deepenScope();
        ArrayList<Instruction> thenBranch = new ArrayList<>(stmt.thenBranch.accept(this));
        out.addAll(freeScope());
        if (stmt.hasElseStatement()) {
            deepenScope();
            elseBranch.addAll(stmt.elseBranch.accept(this));
            out.addAll(freeScope());
        }
        out.add(new IfInstruction(variable, thenBranch, elseBranch));
        return out;
    }
    public ArrayList<Instruction> visitNativeStatement(NativeStatement stmt) {
        ArrayList<Instruction> out = new ArrayList<>();
        out.add(new NativeInstruction(stmt.code));
        return out;
    }
}
