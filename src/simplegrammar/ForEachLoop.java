package simplegrammar;

import java.util.List;

public class ForEachLoop implements Expression {
    private Lookup variable;
    private Lookup sequence;
    private List<Expression> body;

    public ForEachLoop(Lookup variable, Lookup sequence, List<Expression> body) {
        this.variable = variable;
        this.sequence = sequence;
        this.body = body;
    }

    public Lookup getVariable() {
        return this.variable;
    }

    public Lookup getSequence() {
        return this.sequence;
    }

    public List<Expression> getBody() {
        return this.body;
    }

    @Override
    public String nodeName() {
        return "ForEachLoop";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitForEachLoop(this);
    }
}
