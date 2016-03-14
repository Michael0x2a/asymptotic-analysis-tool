package simplegrammar;

public class UnaryOp implements Expression {
    private String operator;
    private Expression value;

    public UnaryOp(String operator, Expression value) {
        this.operator = operator;
        this.value = value;
    }

    public String getOperator() {
        return this.operator;
    }

    public Expression getValue() {
        return this.value;
    }

    @Override
    public String nodeName() {
        return "UnaryOp";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitUnaryOp(this);
    }
}
