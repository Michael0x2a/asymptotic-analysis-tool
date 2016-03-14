package simplegrammar;

public class Type implements AstNode {
    public final String fullType;
    public final boolean isArray;
    private final String underlyingType;

    public Type(String type) {
        this.fullType = notNull(type);
        if (type.endsWith("[]")) {
            this.isArray = true;
            this.underlyingType = type.substring(0, type.length() - 2);
        } else {
            this.isArray = false;
            this.underlyingType = type;
        }
    }

    public String getFullType() {
        return this.fullType;
    }

    public boolean isArray() {
        return this.isArray;
    }

    public String getUnderlyingType() {
        return this.underlyingType;
    }

    @Override
    public String nodeName() {
        return "Type";
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return visitor.visitType(this);
    }
}
