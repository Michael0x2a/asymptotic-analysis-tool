package math;

public class Sum implements MathExpression {
    private MathExpression start;
    private MathExpression end;
    private MathExpression body;
    private Variable index;

    public Sum(MathExpression start, MathExpression end, MathExpression body, Variable index) {
        this.start = start;
        this.end = end;
        this.body = body;
        this.index = index;
    }

    public Variable getIndex() {
        return index;
    }

    public MathExpression getStart() {
        return this.start;
    }

    public MathExpression getEnd() {
        return this.end;
    }

    public MathExpression getBody() {
        return this.body;
    }

    @Override
    public String nodeName() {
        return "sum";
    }

    @Override
    public String toEquation() {
        return String.format("(sum from %s=%s to %s of %s)",
                this.index.toEquation(),
                removeExtraParens(this.start.toEquation()),
                removeExtraParens(this.end.toEquation()),
                this.body.toEquation());
    }

    @Override
    public <T> T accept(MathExpressionVisitor<T> visitor) {
        return visitor.visitSum(this);
    }
}
