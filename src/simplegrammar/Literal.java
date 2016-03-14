package simplegrammar;

public class Literal implements AstNode {
    private String text;

    public Literal(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String nodeName() {
        return "Literal";
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitLiteral(this);
    }
}
