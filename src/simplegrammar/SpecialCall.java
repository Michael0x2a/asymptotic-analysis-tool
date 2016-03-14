package simplegrammar;

import java.util.List;

public class SpecialCall implements Expression {
    private Expression object;
    private String methodName;
    private List<Expression> parameters;

    public SpecialCall(Expression object, String methodName, List<Expression> parameters) {
        this.object = object;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    public Expression getObject() {
        return this.object;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public List<Expression> getParameters() {
        return this.parameters;
    }

    @Override
    public String nodeName() {
        return "SpecialCall";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitSpecialCall(this);
    }
}
