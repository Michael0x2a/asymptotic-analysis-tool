package bigo;

import math.*;
import simplegrammar.Parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VariableSubstitution implements MathExpressionVisitor<MathExpression> {
    private Map<String, MathExpression> paramMap;
    private Map<String, FunctionCall> functionMap;
    private String currentFunctionName;

    public VariableSubstitution(
            String currentFunctionName,
            Map<String, MathExpression> paramMap,
            Map<String, FunctionCall> functionMap) {
        this.currentFunctionName = currentFunctionName;
        this.paramMap = paramMap;
        this.functionMap = functionMap;
    }

    public VariableSubstitution(String currentFunctionName, Map<String, FunctionCall> functionMap) {
        this(currentFunctionName, new HashMap<>(), functionMap);
    }

    private List<MathExpression> visitChildren(List<MathExpression> terms) {
        return terms.stream().map(this::visit).collect(Collectors.toList());
    }

    @Override
    public MathExpression visitAddition(Addition expr) {
        return new Addition(this.visitChildren(expr.getTerms()));
    }

    @Override
    public MathExpression visitConstant(Constant expr) {
        return expr;
    }

    @Override
    public MathExpression visitDivision(Division expr) {
        return new Division(this.visitChildren(expr.getTerms()));
    }

    @Override
    public MathExpression visitFunction(Function expr) {
        System.err.println(expr.getFunctionName() + " " + this.currentFunctionName);
        if (expr.getFunctionName().equals(this.currentFunctionName)) {
            return expr;
        } else {
            FunctionCall functionDefn = this.functionMap.get(expr.getFunctionName());
            System.err.println(expr.getParameters());
            System.err.println(functionDefn.getParameters());
            if (expr.getParameters().size() != functionDefn.getParameters().size()) {
                throw new IllegalStateException("Parameter count mismatch!");
            }

            Map<String, MathExpression> mapping = new HashMap<>();
            for (int i = 0; i < expr.getParameters().size(); i++) {
                MathExpression inputParam = expr.getParameters().get(i);
                Variable funcVar = functionDefn.getParameters().get(i);

                mapping.put(funcVar.getName(), inputParam);
            }

            return new VariableSubstitution(
                    functionDefn.getFunction().getName(),
                    mapping,
                    this.functionMap).visit(functionDefn.getRecursiveCase());
        }
    }

    @Override
    public MathExpression visitLog(Log expr) {
        return new Log(this.visit(expr.getExpression()));
    }

    @Override
    public MathExpression visitMultiplication(Multiplication expr) {
        return new Multiplication(this.visitChildren(expr.getTerms()));
    }

    @Override
    public MathExpression visitNegation(Negation expr) {
        return new Negation(this.visit(expr.getOriginalValue()));
    }

    @Override
    public MathExpression visitSubtraction(Subtraction expr) {
        return new Subtraction(this.visitChildren(expr.getTerms()));
    }

    @Override
    public MathExpression visitVariable(Variable expr) {
        if (!this.paramMap.containsKey(expr.getName())) {
            return expr;
        } else {
            return this.paramMap.get(expr.getName());
        }
    }

    @Override
    public MathExpression visitSum(Sum expr) {
        return new Sum(
                this.visit(expr.getStart()),
                this.visit(expr.getEnd()),
                this.visit(expr.getBody()),
                expr.getIndex());
    }

    @Override
    public MathExpression visitFunctionCall(FunctionCall expr) {
        return new FunctionCall(
                expr.getFunction(),
                expr.getParameters(),
                this.visit(expr.getRecursiveCase()),
                expr.getBaseCase() == null ? null : this.visit(expr.getBaseCase()));
    }
}
