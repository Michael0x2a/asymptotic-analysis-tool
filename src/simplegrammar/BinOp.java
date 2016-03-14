package simplegrammar;

public class BinOp implements AstNode {
    private String operator;
    private AstNode left;
    private AstNode right;

    public BinOp(String operator, AstNode left, AstNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public String getOperator() {
        return this.operator;
    }

    public AstNode getLeft() {
        return this.left;
    }

    public AstNode getRight() {
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
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitBinOp(this);
    }
}
