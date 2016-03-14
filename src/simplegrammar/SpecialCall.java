package simplegrammar;

import java.util.List;

public class SpecialCall implements AstNode {
    private AstNode object;
    private String methodName;
    private List<AstNode> parameters;

    public SpecialCall(AstNode object, String methodName, List<AstNode> parameters) {
        this.object = notNull(object);
        this.methodName = notNull(methodName);
        this.parameters = notNull(parameters);
    }

    public AstNode getObject() {
        return this.object;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public List<AstNode> getParameters() {
        return this.parameters;
    }

    @Override
    public String nodeName() {
        return "SpecialCall";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitSpecialCall(this);
    }
}
