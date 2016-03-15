package math;

import java.util.List;
import java.util.StringJoiner;

public class Multiplication implements MathExpression, MultiTerm {
    private List<MathExpression> terms;

    public Multiplication(List<MathExpression> terms) {
        this.terms = failOnEmpty(terms);
    }

    public List<MathExpression> getTerms() {
        return this.terms;
    }

    @Override
    public String nodeName() {
        return "Multiplication";
    }

    @Override
    public String toEquation() {
        StringJoiner output = new StringJoiner(" * ", "(", ")");
        for (MathExpression e : terms) {
            output.add(e.toEquation());
        }
        return output.toString();
    }

    @Override
    public <T> T accept(MathExpressionVisitor<T> visitor) {
        return visitor.visitMultiplication(this);
    }
}
