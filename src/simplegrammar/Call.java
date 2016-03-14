package simplegrammar;

import java.util.List;

public class Call implements AstNode {
    private String methodName;
    private List<AstNode> parameters;

    public Call(String methodName, List<AstNode> parameters) {
        this.methodName = notNull(methodName);
        this.parameters = notNull(parameters);
    }

    public String getMethodName() {
        return this.methodName;
    }

    public List<AstNode> getParameters() {
        return this.parameters;
    }

    @Override
    public String nodeName() {
        return "Call";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitCall(this);
    }
}
