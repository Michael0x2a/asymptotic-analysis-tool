package simplegrammar;

public class Assignment implements Expression {
    private String name;
    private Expression value;

    public Assignment(String name, Expression value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public Expression getValue() {
        return this.value;
    }

    @Override
    public String nodeName() {
        return "Assignment";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitAssignment(this);
    }
}
