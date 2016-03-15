package bigo;

import math.*;

import java.util.StringJoiner;

public class EquationDisplayPrinter implements MathExpressionVisitor<String> {
    private String removeExtraParens(String eq) {
        while (eq.startsWith("(") && eq.endsWith(")")) {
            eq = eq.substring(1, eq.length() - 1);
        }
        return eq;
    }

    @Override
    public String visitAddition(Addition expr) {
        StringJoiner output = new StringJoiner(" + ", "(", ")");
        for (MathExpression e : expr.getTerms()) {
            output.add(this.visit(e));
        }
        return output.toString();
    }

    @Override
    public String visitConstant(Constant expr) {
        return "" + expr.getValue();
    }

    @Override
    public String visitDivision(Division expr) {
        StringJoiner output = new StringJoiner("/");
        for (MathExpression e : expr.getTerms()) {
            output.add(this.visit(e));
        }
        return output.toString();
    }

    @Override
    public String visitFunction(Function expr) {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        for (MathExpression param : expr.getParameters()) {
            joiner.add(removeExtraParens(this.visit(param)));
        }

        return expr.getFunctionName() + joiner.toString();
    }

    @Override
    public String visitLog(Log expr) {
        return "log(" + this.visit(expr) + ")";
    }

    @Override
    public String visitMultiplication(Multiplication expr) {
        StringJoiner output = new StringJoiner(" * ", "(", ")");
        for (MathExpression e : expr.getTerms()) {
            output.add(this.visit(e));
        }
        return output.toString();
    }

    @Override
    public String visitNegation(Negation expr) {
        return "-" + this.visit(expr.getValue());
    }

    @Override
    public String visitSubtraction(Subtraction expr) {
        StringJoiner output = new StringJoiner("-", "(", ")");
        for (MathExpression e : expr.getTerms()) {
            output.add(this.visit(e));
        }
        return output.toString();
    }

    @Override
    public String visitVariable(Variable expr) {
        return expr.getName();
    }

    @Override
    public String visitSum(Sum expr) {
        return String.format("(sum_(%s=%s)^(%s) %s)",
                this.visit(expr.getIndex()),
                removeExtraParens(this.visit(expr.getStart())),
                removeExtraParens(this.visit(expr.getEnd())),
                this.visit(expr.getBody()));
    }

    @Override
    public String visitFunctionCall(FunctionCall expression) {
        StringJoiner paramJoiner = new StringJoiner(", ", "(", ")");
        for (MathExpression expr : expression.getParameters()) {
            paramJoiner.add(this.visit(expr));
        }
        StringJoiner zeroJoiner = new StringJoiner(", ", "(", ")");
        for (MathExpression expr : expression.getParameters()) {
            zeroJoiner.add("1");
        }

        if (expression.isRecursive()) {
            return String.format("%s%s = %s, %s%s = 1",
                    expression.getFunction().getName(),
                    paramJoiner.toString(),
                    this.visit(expression.getRecursiveCase()),
                    this.visit(expression.getFunction()),
                    zeroJoiner.toString());
        } else {
            return this.visit(expression.getRecursiveCase());
        }
    }
}
