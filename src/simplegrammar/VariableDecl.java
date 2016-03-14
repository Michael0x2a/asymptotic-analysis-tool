package simplegrammar;

public class VariableDecl implements Expression {
    private Type type;
    private String name;

    public VariableDecl(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Type getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String nodeName() {
        return "VariableDecl";
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitVariableDecl(this);
    }
}
