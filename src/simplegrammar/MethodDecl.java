package simplegrammar;

import java.util.List;

public class MethodDecl implements AstNode {
    private Type returnType;
    private String name;
    private List<Parameter> parameters;
    private List<AstNode> body;

    public MethodDecl(Type returnType, String name, List<Parameter> parameters, List<AstNode> body) {
        this.returnType = notNull(returnType);
        this.name = notNull(name);
        this.parameters = notNull(parameters);
        this.body = notNull(body);
    }

    public Type getReturnType() {
        return this.returnType;
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

    public List<AstNode> getBody() {
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
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitMethodDecl(this);
    }
}
