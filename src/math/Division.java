package math;

import java.util.List;
import java.util.StringJoiner;

public class Division implements MathExpression, MultiTerm {
    protected List<MathExpression> terms;

    public Division(List<MathExpression> terms) {
        this.terms = failOnEmpty(terms);
    }

    public List<MathExpression> getTerms() {
        return this.terms;
    }

    @Override
    public String nodeName() {
        return "Division";
    }

    @Override
    public String toEquation() {
        StringJoiner output = new StringJoiner("/");
        for (MathExpression e : terms) {
            output.add(e.toEquation());
        }
        return output.toString();
    }

    @Override
    public <T> T accept(MathExpressionVisitor<T> visitor) {
        return visitor.visitDivision(this);
    }
}
