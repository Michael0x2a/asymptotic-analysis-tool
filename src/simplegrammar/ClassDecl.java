package simplegrammar;

import java.util.List;

public class ClassDecl implements AstNode {
    private final List<MethodDecl> methods;
    private final String name;

    public ClassDecl(String name, List<MethodDecl> methods) {
        this.methods = notNull(methods);
        this.name = notNull(name);
    }

    public String getClassName() {
        return this.name;
    }

    public List<MethodDecl> getMethods() {
        return this.methods;
    }

    public MethodDecl getMethod(String name) {
        for (MethodDecl method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        throw new RuntimeException("Unknown method");
    }

    @Override
    public String nodeName() {
        return "ClassDecl";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitClassDecl(this);
    }
}
