package math;

import java.util.List;

public interface MultiTerm extends MathExpression {
    List<MathExpression> getTerms();
}
