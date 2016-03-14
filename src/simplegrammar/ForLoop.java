package simplegrammar;

import java.util.List;

public class ForLoop implements AstNode {
    private Lookup counter;
    private AstNode end;
    private AstNode change;
    private List<AstNode> body;

    public ForLoop(Lookup counter, AstNode end, AstNode change, List<AstNode> body) {
        this.counter = notNull(counter);
        this.end = notNull(end);
        this.change = notNull(change);
        this.body = notNull(body);
    }

    public Lookup getCounter() {
        return this.counter;
    }

    public AstNode getEnd() {
        return this.end;
    }

    public AstNode getChange() {
        return this.change;
    }

    public List<AstNode> getBody() {
        return this.body;
    }

    @Override
    public String nodeName() {
        return "ForLoop";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitForLoop(this);
    }
}
