package math;

import java.util.ArrayList;
import java.util.Arrays;

public class Negation implements MathExpression {
    private MathExpression expression;
    private MathExpression negatedExpression;

    public Negation(MathExpression expression) {
        this.expression = expression;
        if (expression.nodeName().equals("Constant")) {
            this.negatedExpression = new Constant(-1 * ((Constant) expression).getValue());
        } else {
            this.negatedExpression = new Subtraction(Arrays.asList(new Constant(0), this.expression));
        }
    }

    public MathExpression getValue() {
        return this.negatedExpression;
    }

    public MathExpression getOriginalValue() {
        return this.expression;
    }

    @Override
    public String nodeName() {
        return "Negation";
    }

    @Override
    public String toEquation() {
        return "-" + this.expression.toEquation();
    }
}
