package math;

public class Variable implements MathExpression {
    private String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return this.getName();
    }

    @Override
    public String nodeName() {
        return "Variable";
    }
    @Override
    public String toEquation() {
        return name;
    }

}
