package simplegrammar;

import java.util.List;

public class ForEachLoop implements AstNode {
    private Lookup variable;
    private Lookup sequence;
    private List<AstNode> body;

    public ForEachLoop(Lookup variable, Lookup sequence, List<AstNode> body) {
        this.variable = variable;
        this.sequence = sequence;
        this.body = body;
    }

    public Lookup getVariable() {
        return this.variable;
    }

    public Lookup getSequence() {
        return this.sequence;
    }

    public List<AstNode> getBody() {
        return this.body;
    }

    @Override
    public String nodeName() {
        return "ForEachLoop";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitForEachLoop(this);
    }
}
