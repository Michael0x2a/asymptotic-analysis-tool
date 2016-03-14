package simplegrammar;

import java.util.List;

public class ForLoop implements Expression {
    private Lookup counter;
    private Expression end;
    private Expression change;
    private List<Expression> body;

    public ForLoop(Lookup counter, Expression end, Expression change, List<Expression> body) {
        this.counter = counter;
        this.end = end;
        this.change = change;
        this.body = body;
    }

    public Lookup getCounter() {
        return this.counter;
    }

    public Expression getEnd() {
        return this.end;
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
