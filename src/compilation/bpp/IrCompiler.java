package compilation.bpp;

import compilation.ir.MemberVariable;
import compilation.ir.UniqueVariable;
import compilation.ir.instructions.*;
import compilation.ir.instructions.cf.*;
import compilation.ir.instructions.math.*;
import compilation.ir.instructions.math.num.*;
import compilation.ir.instructions.mem.*;
import main.StructType;

import java.util.ArrayList;

import static compilation.bpp.InstructionCreator.*;

class IrCompilerException extends RuntimeException {
    public IrCompilerException(String s) {
        super(s);
    }
}

public class IrCompiler implements InstructionVisitor<String> {
    public static final int NUM_SPECIAL_REGS = 14;
    public static final int TEMP_REG0 = 0,
            TEMP_REG1 = 1,
            TEMP_REG2 = 2, // reserved
            COMPR = 3,
            COMPC1 = 4,
            COMPC2 = 5,
            COMP1 = 6,
            COMP2 = 7,
            B1 = 8,
            B2 = 9,
            BC = 10,
            BR = 11,
            BOOL_IN = 12,
            BOOL_OUT = 13;
    public static final SimpleRegister  TEMP0 = new SimpleRegister(TEMP_REG0),
            TEMP1 = new SimpleRegister(TEMP_REG1),
            TEMP2 = new SimpleRegister(TEMP_REG2);
    public String compile(ArrayList<Instruction> instructions) {
        StringBuilder sb = new StringBuilder(Builtin.preInclude());
        for (Instruction instr : instructions)
            sb.append(instr.accept(this));
        return sb.toString();
    }
    private final VariableSpace vars = new VariableSpace();
    private Register retrieveVariable(UniqueVariable uv) {
        if (uv instanceof MemberVariable mv) {
            int size = mv.type.getSize();
            Register reg = vars.get(mv.getOwnerName());
            if (!(reg instanceof StructRegister sr))
                throw new IrCompilerException("Invalid variable type");
            if (size == 1)
                return sr.get(mv.offset);
            else {
                ArrayList<SimpleRegister> regs = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    regs.add(sr.get(mv.offset+i));
                }
                return new StructRegister(regs);
            }
        }
        return vars.get(uv.getUniqueName());
    }
    public String visitAllocateInstruction(AllocateInstruction instr) {
        vars.allocate(instr.uv().getUniqueName(), instr.size(), instr.uv().type instanceof StructType);
        return "";
    }
    public String visitFreeInstruction(FreeInstruction instr) {
        Register r = vars.get(instr.name());
        vars.remove(instr.name());
        if (r instanceof SimpleRegister s) {
            return buildClearInstruction(s);
        }
        StringBuilder sb = new StringBuilder();
        StructRegister s = (StructRegister) r;
        for (int i = 0; i < s.length(); i++) {
            sb.append(buildClearInstruction(s.get(i)));
        }
        return sb.toString();
    }
    public String visitAddInstruction(AddInstruction instr) {
        UniqueVariable  uv1 = instr.v1(),
                uv2 = instr.v2(),
                uv3 = instr.result();
        Register r1 = retrieveVariable(uv1);
        if (r1 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r2 = retrieveVariable(uv2);
        if (r2 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r3 = retrieveVariable(uv3);
        if (r3 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        return buildAddInstruction((SimpleRegister) r1, (SimpleRegister) r2, (SimpleRegister) r3);
    }
    public String visitSubInstruction(SubInstruction instr) {
        UniqueVariable  uv1 = instr.v1(),
                uv2 = instr.v2(),
                uv3 = instr.result();
        Register r1 = retrieveVariable(uv1);
        if (r1 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r2 = retrieveVariable(uv2);
        if (r2 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r3 = retrieveVariable(uv3);
        if (r3 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        return buildSubInstruction((SimpleRegister) r1, (SimpleRegister) r2, (SimpleRegister) r3);
    }
    public String visitMulInstruction(MulInstruction instr) {
        UniqueVariable  uv1 = instr.v1(),
                uv2 = instr.v2(),
                uv3 = instr.result();
        Register r1 = retrieveVariable(uv1);
        if (r1 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r2 = retrieveVariable(uv2);
        if (r2 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r3 = retrieveVariable(uv3);
        if (r3 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        return buildMulInstruction((SimpleRegister) r1, (SimpleRegister) r2, (SimpleRegister) r3);
    }
    public String visitPrintInstruction(PrintInstruction instr) {
        UniqueVariable uv = instr.var();
        Register r = retrieveVariable(uv);
        if (!(r instanceof SimpleRegister sr))
            throw new IrCompilerException("Invalid data type");
        return buildPrintInstruction(sr);
    }
    public String visitCopyInstruction(CopyInstruction instr) {
        UniqueVariable v1 = instr.var1();
        UniqueVariable v2 = instr.var2();
        Register r1 = retrieveVariable(v1);
        Register r2 = retrieveVariable(v2);
        if (r1 instanceof StructRegister sr1) {
            if (!(r2 instanceof StructRegister sr2) || sr1.length() != sr2.length())
                throw new IrCompilerException("Invalid operands");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < sr1.length(); i++) {
                sb.append(buildCopyInstruction(sr1.get(i), sr2.get(i)));
            }
            return sb.toString();
        }
        return buildCopyInstruction((SimpleRegister) r1, (SimpleRegister) r2);
    }
    public String visitWhileInstruction(WhileInstruction instr) {
        StringBuilder body = new StringBuilder();
        Register r = retrieveVariable(instr.var());
        if (!(r instanceof SimpleRegister sr))
            throw new IrCompilerException("Invalid operand");
        for (Instruction i : instr.instruction()) {
            body.append(i.accept(this));
        }
        return buildWhileInstruction(sr, body.toString());
    }
    public String visitIfInstruction(IfInstruction instr) {
        throw new IrCompilerException("Invalid instruction");
    }
    public String visitClearInstruction(ClearInstruction instr) {
        Register r = retrieveVariable(instr.register());
        if (r instanceof StructRegister sr) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < sr.length(); i++) {
                sb.append(buildClearInstruction(sr.get(i)));
            }
            return sb.toString();
        }
        return buildClearInstruction((SimpleRegister) r);
    }
    public String visitNativeInstruction(NativeInstruction instr) {
        return instr.str();
    }
    public String visitFunctionDefinitionInstruction(FunctionDefinitionInstruction instr) {
        throw new IrCompilerException("Invalid instruction");
    }
    public String visitDivInstruction(DivInstruction instr) {
        Register r1 = retrieveVariable(instr.v1());
        Register r2 = retrieveVariable(instr.v2());
        Register r3 = retrieveVariable(instr.result());
        if (!(r1 instanceof SimpleRegister s1))
            throw new IrCompilerException("Invalid operands");
        if (!(r2 instanceof SimpleRegister s2))
            throw new IrCompilerException("Invalid operands");
        if (!(r3 instanceof SimpleRegister s3))
            throw new IrCompilerException("Invalid operands");
        return buildDivModInstruction(s1, s2, s3, TEMP0)+buildClearInstruction(TEMP0);
    }
    public String visitModInstruction(ModInstruction instr) {
        Register r1 = retrieveVariable(instr.v1());
        Register r2 = retrieveVariable(instr.v2());
        Register r3 = retrieveVariable(instr.result());
        if (!(r1 instanceof SimpleRegister s1))
            throw new IrCompilerException("Invalid operands");
        if (!(r2 instanceof SimpleRegister s2))
            throw new IrCompilerException("Invalid operands");
        if (!(r3 instanceof SimpleRegister s3))
            throw new IrCompilerException("Invalid operands");
        return buildDivModInstruction(s1, s2, TEMP0, s3)+buildClearInstruction(TEMP0);
    }
    public String visitExpInstruction(ExpInstruction instr) {
        Register r1 = retrieveVariable(instr.v1());
        Register r2 = retrieveVariable(instr.v2());
        Register r3 = retrieveVariable(instr.result());
        if (!(r1 instanceof SimpleRegister s1))
            throw new IrCompilerException("Invalid operands");
        if (!(r2 instanceof SimpleRegister s2))
            throw new IrCompilerException("Invalid operands");
        if (!(r3 instanceof SimpleRegister s3))
            throw new IrCompilerException("Invalid operands");
        StringBuilder sb = new StringBuilder();
        sb.append(buildCopyInstruction(s2, TEMP0));
        sb.append(buildPutInstruction(1, TEMP1));
        StringBuilder loop = new StringBuilder();
        loop.append(buildMulInstruction(s1, TEMP1, TEMP1));
        loop.append(buildDecInstruction(TEMP0, 1));
        sb.append(buildWhileInstruction(TEMP0, loop.toString()));
        sb.append(buildMoveInstruction(TEMP1, s3));
        return sb.toString();
    }
    public String visitEqInstruction(EqInstruction instr) {
        Register r1 = retrieveVariable(instr.v1());
        Register r2 = retrieveVariable(instr.v2());
        Register r3 = retrieveVariable(instr.result());
        if (!(r3 instanceof SimpleRegister s3))
            throw new IrCompilerException("Invalid result");
        if (r1 instanceof StructRegister sr1) {
            if (!(r2 instanceof StructRegister sr2) || sr1.length() != sr2.length())
                throw new IrCompilerException("Invalid operands");
            StringBuilder sb = new StringBuilder();
            sb.append(buildPutInstruction(1, s3));
            for (int i = 0; i < sr1.length(); i++) {
                sb.append(buildEqInstruction(sr1.get(i), sr2.get(i), TEMP0));
                sb.append(buildAndInstruction(TEMP0, s3, s3));
            }
            return sb.toString();
        }
        return buildEqInstruction((SimpleRegister) r1, (SimpleRegister) r2, s3);
    }
    public String visitInvInstruction(InvInstruction instr) {
        Register r1 = retrieveVariable(instr.v());
        Register r2 = retrieveVariable(instr.result());
        if (!(r1 instanceof SimpleRegister s1) || !(r2 instanceof SimpleRegister s2))
            throw new IrCompilerException("Invalid operands");
        return buildInvInstruction(s1, s2);
    }
    public String visitAndInstruction(AndInstruction instr) {
        UniqueVariable  uv1 = instr.v1(),
                uv2 = instr.v2(),
                uv3 = instr.result();
        Register r1 = retrieveVariable(uv1);
        if (r1 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r2 = retrieveVariable(uv2);
        if (r2 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r3 = retrieveVariable(uv3);
        if (r3 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        return buildAndInstruction((SimpleRegister) r1, (SimpleRegister) r2, (SimpleRegister) r3);
    }
    public String visitOrInstruction(OrInstruction instr) {
        UniqueVariable  uv1 = instr.v1(),
                uv2 = instr.v2(),
                uv3 = instr.result();
        Register r1 = retrieveVariable(uv1);
        if (r1 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r2 = retrieveVariable(uv2);
        if (r2 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r3 = retrieveVariable(uv3);
        if (r3 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        return buildOrInstruction((SimpleRegister) r1, (SimpleRegister) r2, (SimpleRegister) r3);
    }
    public String visitXorInstruction(XorInstruction instr) {
        UniqueVariable  uv1 = instr.v1(),
                uv2 = instr.v2(),
                uv3 = instr.result();
        Register r1 = retrieveVariable(uv1);
        if (r1 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r2 = retrieveVariable(uv2);
        if (r2 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r3 = retrieveVariable(uv3);
        if (r3 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        return buildXorInstruction((SimpleRegister) r1, (SimpleRegister) r2, (SimpleRegister) r3);
    }
    public String visitGtInstruction(GtInstruction instr) {
        UniqueVariable  uv1 = instr.v1(),
                uv2 = instr.v2(),
                uv3 = instr.result();
        Register r1 = retrieveVariable(uv1);
        if (r1 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r2 = retrieveVariable(uv2);
        if (r2 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        Register r3 = retrieveVariable(uv3);
        if (r3 instanceof StructRegister)
            throw new IrCompilerException("Invalid operands");
        return buildGtInstruction((SimpleRegister) r1, (SimpleRegister) r2, (SimpleRegister) r3);
    }
    public String visitNegInstruction(NegInstruction instr) {
        Register r1 = retrieveVariable(instr.v());
        Register r2 = retrieveVariable(instr.result());
        if (!(r1 instanceof SimpleRegister s1) || !(r2 instanceof SimpleRegister s2))
            throw new IrCompilerException("Invalid operands");
        return buildClearInstruction(TEMP0)+buildSubInstruction(TEMP0, s1, s2);
    }
    public String visitBoolInstruction(BoolInstruction instr) {
        Register r1 = retrieveVariable(instr.v());
        Register r2 = retrieveVariable(instr.result());
        if (!(r1 instanceof SimpleRegister s1) || !(r2 instanceof SimpleRegister s2))
            throw new IrCompilerException("Invalid operands");
        return buildBoolInstruction(s1, s2);
    }
    public String visitPrintstrInstruction(PrintstrInstruction instr) {
        String s = instr.str();
        return buildPrintstrInstruction(s);
    }
    public String visitPutInstruction(PutInstruction instr) {
        Register r = retrieveVariable(instr.variable());
        int val = instr.value();
        if (!(r instanceof SimpleRegister s))
            throw new IrCompilerException("Invalid operand");
        return buildPutInstruction(val, s);
    }
    public String visitCallInstruction(CallInstruction instr) {
        throw new IrCompilerException("Invalid instruction");
    }
    public String visitSimpleIfInstruction(SimpleIfInstruction instr) {
        throw new IrCompilerException("Invalid instruction");
    }
    public String visitInputInstruction(InputInstruction instr) {
        Register r = retrieveVariable(instr.uv());
        if (!(r instanceof SimpleRegister s))
            throw new IrCompilerException("Invalid operand");
        return buildInputInstruction(s);
    }
}
