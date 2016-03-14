package simplegrammar;

public class UnaryOp implements AstNode {
    private String operator;
    private AstNode value;

    public UnaryOp(String operator, AstNode value) {
        this.operator = operator;
        this.value = value;
    }

    public String getOperator() {
        return this.operator;
    }

    public AstNode getValue() {
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
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitUnaryOp(this);
    }
}
