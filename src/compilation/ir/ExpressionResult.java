package compilation.ir;

import compilation.ir.instructions.Instruction;

import java.util.*;

public record ExpressionResult(ArrayList<Instruction> instructions, UniqueVariable result) {}
