package simplegrammar;

import java.util.List;

public class ForLoop implements Expression {
    private Lookup counter;
    private Lookup sequence;
    private Expression change;
    private List<Expression> body;

    public ForLoop(Lookup counter, Lookup sequence, Expression change, List<Expression> body) {
        this.counter = counter;
        this.sequence = sequence;
        this.change = change;
        this.body = body;
    }

    public Lookup getCounter() {
        return this.counter;
    }

    public Lookup getSequence() {
        return this.sequence;
    }

    public Expression getChange() {
        return this.change;
    }

    public List<Expression> getBody() {
        return this.body;
    }

    @Override
    public String nodeName() {
        return "ForLoop";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitForLoop(this);
    }
}
