package simplegrammar;

public abstract class AstNodeVisitor<T> {
    public abstract T visitClassDecl(ClassDecl node);
    public abstract T visitMethodDecl(MethodDecl node);
    public abstract T visitParameter(Parameter node);
    public abstract T visitVariableDecl(VariableDecl node);
    public abstract T visitAssignment(Assignment node);
    public abstract T visitLookup(Lookup node);
    public abstract T visitForEachLoop(ForEachLoop node);
    public abstract T visitForLoop(ForLoop node);
    public abstract T visitReturn(Return node);
    public abstract T visitIfElse(IfElse node);
    public abstract T visitSpecialCall(SpecialCall node);
    public abstract T visitCall(Call node);
    public abstract T visitBinOp(BinOp node);
    public abstract T visitUnaryOp(UnaryOp node);
    public abstract T visitLiteral(Literal node);
    public abstract T visitType(Type node);

    public T visit(AstNode expr) {
        return expr.accept(this);
    }
}
