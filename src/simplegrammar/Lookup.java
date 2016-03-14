package simplegrammar;

public class Lookup implements AstNode {
    private String name;

    public Lookup(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String nodeName() {
        return "Lookup";
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitLookup(this);
    }
}
