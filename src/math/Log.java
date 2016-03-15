package math;

public class Log implements MathExpression {
    private MathExpression expression;

    public Log(MathExpression expression) {
        this.expression = expression;
    }

    public MathExpression getExpression() {
        return this.expression;
    }


    @Override
    public String nodeName() {
        return "Log";
    }

    @Override
    public String toEquation() {
        return "log(" + expression.toEquation() + ")";
    }

}
