package bigo;

import java.util.*;
import java.util.function.Supplier;

import math.*;
import simplegrammar.*;

public class OutputComplexityVisitor extends AstNodeVisitor<MathExpression> {
    // Maps java variables to abstract symbols
    private Map<String, Variable> assumptions;

    // Maps java variables to math expressions representing their basic output complexities
    private Map<String, MathExpression> variables;

    private final Supplier<String> variableGenId;

    private static final String[] VARIABLE_NAMES = {
            "n", "m", "x", "y", "z", "r", "s",
            "t", "u", "v", "a", "b", "c", "d",
            "e", "f", "g", "h", "i", "j", "k",
            "l", "p", "q", "w"};

    public OutputComplexityVisitor() {
        this.assumptions = new HashMap<>();
        this.variables = new HashMap<>();
        Queue<String> variableSymbols = new LinkedList<>(Arrays.asList(VARIABLE_NAMES));
        this.variableGenId = variableSymbols::remove;
    }

    public Variable lookupAssumption(Lookup variable) {
        return this.assumptions.get(variable.getName());
    }

    public Variable lookupAssumption(Parameter variable) {
        return this.assumptions.get(variable.getName());
    }

    public Variable recordAssumption(Lookup variable) {
        Variable var = new Variable(this.variableGenId.get());
        this.assumptions.put(variable.getName(), var);
        return var;
    }

    public Variable recordAssumption(Parameter variable) {
        Variable var = new Variable(this.variableGenId.get());
        this.assumptions.put(variable.getName(), var);
        return var;
    }

    public MathExpression recordVariable(Assignment asgn) {
        MathExpression expr = this.visit(asgn.getValue());
        this.variables.put(asgn.getName(), expr);
        return expr;
    }
    
    public MathExpression recordVariable(String variableName, MathExpression expr) {
    	this.variables.put(variableName, expr);
    	return expr;
    }

    public MathExpression lookupExpression(AstNode variable) {
        return this.visit(variable);
    }

    @Override
    public MathExpression visitClassDecl(ClassDecl node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MathExpression visitMethodDecl(MethodDecl node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MathExpression visitParameter(Parameter node) {
        return this.recordAssumption(node);
    }

    @Override
    public MathExpression visitVariableDecl(VariableDecl node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MathExpression visitAssignment(Assignment node) {
        MathExpression expr = this.visit(node.getValue());
        this.variables.put(node.getName(), expr);
        return expr;
    }

    @Override
    public MathExpression visitLookup(Lookup node) {
        String javaName = node.getName();

        // Check variables first, in case we override assumptions
        if (this.variables.containsKey(javaName)) {
            return this.variables.get(javaName);
        } else if (this.assumptions.containsKey(javaName)) {
            return this.assumptions.get(javaName);
        } else {
            return this.recordAssumption(node);
        }
    }

    @Override
    public MathExpression visitForEachLoop(ForEachLoop node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MathExpression visitForLoop(ForLoop node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MathExpression visitReturn(Return node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MathExpression visitIfElse(IfElse node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MathExpression visitSpecialCall(SpecialCall node) {
        AstNode object = node.getObject();
        String methodName = node.getMethodName();
        List<AstNode> parameters = node.getParameters();

        if (object.nodeName().equals("Lookup")) {
            String varName = ((Lookup)object).getName();
            if (methodName.equals("length")) {
                return assumptions.get(varName) != null ? assumptions.get(varName) : variables.get(varName);
            } else if (methodName.equals("arrayget")) {
                varName = varName + "arrayget";
                if (!assumptions.containsKey(varName)) {
                    assumptions.put(varName, new Variable(this.variableGenId.get()));
                }
                return assumptions.get(varName);
            } else if (methodName.equals("arrayput")) {
                throw new AssertionError();
            }
        } else if (methodName.equals("arraycreation")) {
            Type arrayType = (Type) parameters.get(0);
            AstNode length = parameters.get(1);
            return this.visit(length);
        }
        
        throw new UnsupportedOperationException("Unsupported special call: " + methodName);
    }

    @Override
    public MathExpression visitCall(Call node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MathExpression visitBinOp(BinOp node) {
        List<MathExpression> list = new ArrayList<>();
        list.add(visit(node.getLeft()));
        list.add(visit(node.getRight()));

        switch (node.getOperator()) {
            case "+": return new Addition(list);
            case "-": return new Subtraction(list);
            case "*": return new Multiplication(list);
            case "/": return new Division(list);
        }

        throw new UnsupportedOperationException("The " + node.getOperator() + " symbol is currently not supported");
    }

    @Override
    public MathExpression visitUnaryOp(UnaryOp node) {
        MathExpression e = visit(node.getValue());

        switch (node.getOperator()) {
            case "-": return new Negation(e);
        }

        throw new UnsupportedOperationException("The " + node.getOperator() + " symbol is currently not supported");
    }

    @Override
    public MathExpression visitLiteral(Literal node) {
        try {
            return new Constant(Integer.parseInt(node.getText()));
        } catch (NumberFormatException e) {
            return new Constant(1);
            //throw new UnsupportedOperationException();
        }
    }

    @Override
    public MathExpression visitType(Type node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MathExpression visitMultipleAstNodes(MultipleAstNodes node) {
        throw new UnsupportedOperationException();
    }
    
    public Map<String, Variable> getAssumptions() {
    	return assumptions;
    }
    
    public Map<String, MathExpression> getVariables() {
    	return variables;
    }

}
