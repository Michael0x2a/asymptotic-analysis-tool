package bigo;

import math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class EquationSimplifier implements MathExpressionVisitor<MathExpression> {
    private List<Constant> collectConstants(List<MathExpression> expressions) {
        return expressions.stream()
                .filter(e -> e instanceof Constant)
                .map(e -> (Constant) e)
                .collect(Collectors.toList());
    }

    private List<MathExpression> collectNonConstants(List<MathExpression> expressions) {
        return expressions.stream()
                .filter(e -> !(e instanceof Constant))
                .collect(Collectors.toList());
    }

    private List<MathExpression> simplify(List<MathExpression> expressions) {
        return expressions.stream().map(this::visit).collect(Collectors.toList());
    }

    private List<MathExpression> foldUp(Class<? extends MultiTerm> target, List<MathExpression> exprs) {
        List<MathExpression> output = new ArrayList<>();
        for (MathExpression expr : exprs) {
            if (expr.getClass().equals(target)) {
                output.addAll(target.cast(expr).getTerms());
            } else {
                output.add(expr);
            }
        }
        return output;
    }

    private Optional<Constant> mergeConstants(List<Constant> constants, BinaryOperator<Integer> op) {
        return constants.stream()
                .map(Constant::getValue)
                .reduce(op)
                .map(Constant::new);
    }

    private List<MathExpression> mergeConstantIfAble(List<MathExpression> exprs, Optional<Constant> constant) {
        if (constant.isPresent()) {
            exprs.add(constant.get());
        }
        return exprs;
    }

    private List<MathExpression> collapse(Class<? extends MultiTerm> collapseClass, List<MathExpression> expr, BinaryOperator<Integer> op) {
        List<MathExpression> terms = this.simplify(expr);
        terms = this.foldUp(collapseClass, terms);
        List<MathExpression> nonConstants = this.collectNonConstants(terms);
        List<Constant> constants = this.collectConstants(terms);
        Optional<Constant> constant = this.mergeConstants(constants, op);
        return this.mergeConstantIfAble(nonConstants, constant);
    }

    @Override
    public MathExpression visitAddition(Addition expr) {
        return new Addition(this.collapse(Addition.class, expr.getTerms(), (a, b) -> a + b));
    }

    @Override
    public MathExpression visitConstant(Constant expr) {
        return expr;
    }

    @Override
    public MathExpression visitDivision(Division expr) {
        return new Division(this.simplify(expr.getTerms()));
    }

    @Override
    public MathExpression visitFunction(Function expr) {
        return new Function(expr.getFunctionName(), this.simplify(expr.getParameters()));
    }

    @Override
    public MathExpression visitLog(Log expr) {
        return new Log(this.visit(expr.getExpression()));
    }

    @Override
    public MathExpression visitMultiplication(Multiplication expr) {
        return new Multiplication(this.collapse(Multiplication.class, expr.getTerms(), (a, b) -> a * b));
    }

    @Override
    public MathExpression visitNegation(Negation expr) {
        return new Negation(this.visit(expr.getOriginalValue()));
    }

    @Override
    public MathExpression visitSubtraction(Subtraction expr) {
        return new Subtraction(this.collapse(Subtraction.class, expr.getTerms(), (a, b) -> a - b));
    }

    @Override
    public MathExpression visitVariable(Variable expr) {
        return expr;
    }

    @Override
    public MathExpression visitSum(Sum expr) {
        return new Sum(
                this.visit(expr.getStart()),
                this.visit(expr.getEnd()),
                this.visit(expr.getBody()),
                expr.getIndex());
    }
}
