package simplegrammar;

import java.util.List;

public class IfElse implements AstNode {
    private AstNode condition;
    private List<AstNode> trueBranch;
    private List<AstNode> falseBranch;

    public IfElse(AstNode condition, List<AstNode> trueBranch, List<AstNode> falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public List<AstNode> getTrueBranch() {
        return this.trueBranch;
    }

    public List<AstNode> getFalseBranch() {
        return this.falseBranch;
    }

    @Override
    public String nodeName() {
        return "IfElse";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitIfElse(this);
    }
}
