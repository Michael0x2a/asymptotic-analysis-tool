package math;

import java.util.List;
import java.util.StringJoiner;

public class Multiplication implements MathExpression {
    private List<MathExpression> terms;

    public Multiplication(List<MathExpression> terms) {
        this.terms = terms;
    }

    private List<MathExpression> getTerms() {
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
}
