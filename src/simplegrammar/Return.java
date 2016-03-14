package simplegrammar;

public class Return implements Expression {
    private Expression value;

    public Return(Expression value) {
        this.value = value;
    }

    public Expression getValue() {
        return this.value;
    }

    @Override
    public String nodeName() {
        return "Return";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitReturn(this);
    }
}
