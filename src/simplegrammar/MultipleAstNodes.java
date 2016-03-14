package simplegrammar;

import java.util.Arrays;
import java.util.List;

public class MultipleAstNodes implements AstNode {
    List<AstNode> nodes;

    public MultipleAstNodes(List<AstNode> nodes) {
        this.nodes = notNull(nodes);
        for (AstNode n : nodes) {
            notNull(n);
        }
    }

    public MultipleAstNodes(AstNode... nodes) {
        this(Arrays.asList(nodes));
    }

    public List<AstNode> getNodes() {
        return this.nodes;
    }

    @Override
    public String nodeName() {
        return "MultipleAstNodes";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitMultipleAstNodes(this);
    }
}
