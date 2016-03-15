package math;

import java.util.List;
import java.util.StringJoiner;

public class RecursiveCall implements MathExpression {
    private Variable function;
    private List<Variable> parameters;
    private MathExpression recursiveCase;
    private MathExpression baseCase;

    public RecursiveCall(Variable function, List<Variable> parameters, MathExpression recursiveCase, MathExpression baseCase) {
        this.function = function;
        this.parameters = parameters;
        this.recursiveCase = recursiveCase;
        this.baseCase = baseCase;
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
        return "RecursiveCall";
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


        return String.format("%s%s = %s, %s%s = 1",
                this.function.getName(),
                paramJoiner.toString(),
                this.recursiveCase.toEquation(),
                this.function.getName(),
                zeroJoiner.toString());
    }

    @Override
    public <T> T accept(MathExpressionVisitor<T> visitor) {
        return visitor.visitRecursiveCall(this);
    }
}
