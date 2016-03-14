package simplegrammar;

public class Parameter implements Expression {
    private String name;
    private Type type;

    private Parameter(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String nodeName() {
        return "Parameter";
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitParameter(this);
    }
}
