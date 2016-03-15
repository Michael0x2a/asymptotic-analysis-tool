package math;

public interface MathExpressionVisitor<T> {
    T visitAddition(Addition expr);
    T visitConstant(Constant expr);
    T visitDivision(Division expr);
    T visitFunction(Function expr);
    T visitLog(Log expr);
    T visitMultiplication(Multiplication expr);
    T visitNegation(Negation expr);
    T visitSubtraction(Subtraction expr);
    T visitVariable(Variable expr);
    T visitSum(Sum expr);
    T visitFunctionCall(FunctionCall expr);

    default T visit(MathExpression expr) {
        return expr.accept(this);
    }

}
