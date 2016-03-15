package math;

import java.util.List;
import java.util.StringJoiner;

public class FunctionCall implements MathExpression {
    private Variable function;
    private List<Variable> parameters;
    private MathExpression recursiveCase;
    private MathExpression baseCase;

    public FunctionCall(Variable function, List<Variable> parameters, MathExpression recursiveCase, MathExpression baseCase) {
        this.function = function;
        this.parameters = parameters;
        this.recursiveCase = recursiveCase;
        this.baseCase = baseCase;
    }

    public FunctionCall(String function, List<Variable> parameters, MathExpression recursiveCase) {
        this(new Variable(function), parameters, recursiveCase, null);
    }

    public boolean isRecursive() {
        return this.baseCase != null;
    }

    public Variable getFunction() {
        return this.function;
    }

    public List<Variable> getParameters() {
        return this.parameters;
    }

    public MathExpression getRecursiveCase() {
        return this.recursiveCase;
    }

    public MathExpression getBaseCase() {
        return this.baseCase;
    }

    @Override
    public String nodeName() {
        return "FunctionCall";
    }

    @Override
    public String toEquation() {
        StringJoiner paramJoiner = new StringJoiner(", ", "(", ")");
        for (MathExpression expr : this.getParameters()) {
            paramJoiner.add(expr.toEquation());
        }
        StringJoiner zeroJoiner = new StringJoiner(", ", "(", ")");
        for (MathExpression expr : this.getParameters()) {
            zeroJoiner.add("1");
        }

        if (this.isRecursive()) {
            return String.format("%s%s = %s, %s%s = 1",
                    this.function.getName(),
                    paramJoiner.toString(),
                    this.recursiveCase.toEquation(),
                    this.function.getName(),
                    zeroJoiner.toString());
        } else {
            return this.recursiveCase.toEquation();
        }
    }

    @Override
    public <T> T accept(MathExpressionVisitor<T> visitor) {
        return visitor.visitFunctionCall(this);
    }
}
