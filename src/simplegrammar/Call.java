package simplegrammar;

import java.util.List;

public class Call implements Expression {
    private String methodName;
    private List<Expression> parameters;

    public Call(String methodName, List<Expression> parameters) {
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public List<Expression> getParameters() {
        return this.parameters;
    }

    @Override
    public String nodeName() {
        return "Call";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitCall(this);
    }
}
