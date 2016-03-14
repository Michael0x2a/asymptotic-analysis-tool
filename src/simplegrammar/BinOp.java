package simplegrammar;

public class BinOp implements Expression {
    private String operator;
    private Expression left;
    private Expression right;

    public BinOp(String operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public String getOperator() {
        return this.operator;
    }

    public Expression getLeft() {
        return this.left;
    }

    public Expression getRight() {
        return this.right;
    }

    @Override
    public String nodeName() {
        return "BinOp";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitBinOp(this);
    }
}
