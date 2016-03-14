package simplegrammar;

public interface ExpressionVisitor<T> {
    T visitClassDecl(ClassDecl node);
    T visitMethodDecl(MethodDecl node);
    T visitParameter(Parameter node);
    T visitVariableDecl(VariableDecl node);
    T visitAssignment(Assignment node);
    T visitLookup(Lookup node);
    T visitForEachLoop(ForEachLoop node);
    T visitForLoop(ForLoop node);
    T visitReturn(Return node);
    T visitIfElse(IfElse node);
    T visitSpecialCall(SpecialCall node);
    T visitCall(Call node);
    T visitBinOp(BinOp node);
    T visitUnaryOp(UnaryOp node);
    T visitLiteral(Literal node);
    T visit(Expression expr);
}
