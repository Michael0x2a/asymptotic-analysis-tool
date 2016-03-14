package simplegrammar;

import java.util.List;

public class MethodDecl implements Expression {
    private String name;
    private List<Parameter> parameters;
    private List<Expression> body;

    public MethodDecl(String name, List<Parameter> parameters, List<Expression> body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public String getName() {
        return this.name;
    }

    public List<Parameter> getParameters() {
        return this.parameters;
    }

    public Parameter getParameter(String name) {
        return null;
    }

    public List<Expression> getBody() {
        return this.body;
    }

    @Override
    public String nodeName() {
        return "MethodDecl";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitMethodDecl(this);
    }
}
