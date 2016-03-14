package simplegrammar;

import java.util.List;

public class IfElse implements Expression {
    private Expression condition;
    private List<Expression> trueBranch;
    private List<Expression> falseBranch;

    public IfElse(Expression condition, List<Expression> trueBranch, List<Expression> falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    public Expression getCondition() {
        return this.condition;
    }

    public List<Expression> getTrueBranch() {
        return this.trueBranch;
    }

    public List<Expression> getFalseBranch() {
        return this.falseBranch;
    }

    @Override
    public String nodeName() {
        return "IfElse";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitIfElse(this);
    }
}
