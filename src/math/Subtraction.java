package math;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Subtraction implements MathExpression {
    private List<MathExpression> terms;

    public Subtraction(List<MathExpression> terms) {
        this.terms = terms;
    }

    public List<MathExpression> getTerms() {
        return this.terms;
    }

    @Override
    public String nodeName() {
        return "Subtraction";
    }

    @Override
    public String toEquation() {
        StringJoiner output = new StringJoiner("-", "(", ")");
        for (MathExpression e : terms) {
            output.add(e.toEquation());
        }
        return output.toString();
    }
}
