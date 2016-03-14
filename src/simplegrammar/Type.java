package simplegrammar;

public class Type {
    public final String fullType;
    public final boolean isArray;
    public final String underlyingType;

    public Type(String type) {
        this.fullType = type;
        if (type.endsWith("[]")) {
            this.isArray = true;
            this.underlyingType = type.substring(0, type.length() - 2);
        } else {
            this.isArray = false;
            this.underlyingType = type;
        }
    }

}
