package compilation.ir;

import compilation.ir.instructions.Instruction;

public record TempAllocationResult(String tempName, Instruction instr) {
}
