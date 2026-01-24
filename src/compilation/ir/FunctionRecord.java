package compilation.ir;

import main.VariableType;

import java.util.ArrayList;

public record FunctionRecord(String name, ArrayList<VariableType> args) {}
