package math;

import java.util.List;
import java.util.StringJoiner;

public class Function implements MathExpression {
    private String functionName;
    private List<MathExpression> parameters;

    public Function(String functionName, List<MathExpression> parameters) {
        this.functionName = functionName;
        this.parameters = parameters;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public List<MathExpression> getParameters() {
        return this.parameters;
    }

    @Override
    public String nodeName() {
        return "Function";
    }

    @Override
    public String toEquation() {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        for (MathExpression param : this.parameters) {
            joiner.add(param.toEquation());
        }

        return this.functionName + joiner.toString();
    }

    @Override
    public <T> T accept(MathExpressionVisitor<T> visitor) {
        return visitor.visitFunction(this);
    }
}
