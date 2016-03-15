package bigo;

import simplegrammar.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import math.Addition;
import math.Constant;
import math.Division;
import math.MathExpression;
import math.Multiplication;
import math.Subtraction;
import math.Variable;
import math.Function;

public class BigOVisitor extends AstNodeVisitor<MathExpression> {
    private OutputComplexityVisitor outputComplexity;
    private Map<String, MathExpression> methodRuntimeComplexities;
    private Map<String, Variable> methodSymbolVariables;

    private final Supplier<String> functionGenId;

    private static final String[] FUNCTION_NAME = {
            "T", "S", "F", "N", "M", "A", "B",
            "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "P", "Q", "R", "U",
            "V", "W", "X", "Y", "Z"};

    public BigOVisitor() {
        this.outputComplexity = new OutputComplexityVisitor();
        this.methodRuntimeComplexities = new HashMap<>();
        this.methodRuntimeComplexities.put("System.out.println", new Constant(1));
        this.methodSymbolVariables = new HashMap<>();
        Queue<String> functionSymbols = new LinkedList<>(Arrays.asList(FUNCTION_NAME));
        this.functionGenId = functionSymbols::remove;
    }

    @Override
    public MathExpression visitClassDecl(ClassDecl node) {
        List<MethodDecl> methods = node.getMethods();
        if (methods.isEmpty()) {
            throw new IllegalArgumentException("Class needs at least one method");
        }
        // Forward declare all variables
        for (MethodDecl method : methods) {
            this.methodSymbolVariables.put(method.getName(), new Variable(this.functionGenId.get()));
        }
        for (MethodDecl method : methods) {
            this.methodRuntimeComplexities.put(method.getName(), this.visit(method));
        }
        return this.methodRuntimeComplexities.get(methods.get(0).getName());
    }

    @Override
    public MathExpression visitMethodDecl(MethodDecl node) {
        // Add parameters to our assumptions
        for (Parameter p : node.getParameters()) {
            this.outputComplexity.recordAssumption(p);
        }

        // Visit body
        return new Addition(addAstNodes(node.getBody()));
    }

    @Override
    public MathExpression visitParameter(Parameter node) {
        throw new IllegalStateException("tried to find Big-O of parameter declaration");
    }

    @Override
    public MathExpression visitVariableDecl(VariableDecl node) {
        return new Constant(1);
    }

    @Override
    public MathExpression visitAssignment(Assignment node) {
        this.outputComplexity.recordVariable(node);
        return this.visit(node.getValue());
    }

    @Override
    public MathExpression visitLookup(Lookup node) {
        return new Constant(2);
    }

    @Override
    public MathExpression visitForEachLoop(ForEachLoop node) {
        MathExpression bodyRuntime = this.addAstNodes(node.getBody());
        MathExpression outerRuntime;

        AstNode sequence = node.getSequence();
        switch(sequence.nodeName()) {
            case "Lookup":
                outerRuntime = this.outputComplexity.lookupExpression((Lookup) sequence);
                break;
            default:
                throw new UnsupportedOperationException("what's " + node.nodeName() + "?");
        }

        return new Multiplication(Arrays.asList(outerRuntime, bodyRuntime));
    }

    @Override
    public MathExpression visitForLoop(ForLoop node) {
    	AstNode counter = node.getCounter();
    	AstNode end = node.getEnd();
    	AstNode change = node.getChange();
    	MathExpression counterComplexity, endComplexity, changeComplexity;
    	String loopVariable;
    	switch (counter.nodeName()) {
    		case "Assignment": 
	    		counterComplexity = this.outputComplexity.visit(((Assignment)counter).getValue());
	    		loopVariable = ((Assignment)counter).getName();
	    		break;
    		default:
    			throw new IllegalArgumentException(counter.nodeName() + " is not supported in for loop counters");
    	}
    	
    	switch (end.nodeName()) {
	    	case "BinOp":
	    		switch(((BinOp)end).getOperator()) {
		    		case "<":
		    		case "<=":
		    		case ">":
		    		case ">=":
		    			// Expect to find loopVariable on one side. Evaluate the output complexity of the other side.
		    			AstNode left = ((BinOp)end).getLeft();
		    			AstNode right = ((BinOp)end).getRight();
		    			AstNode eval = null;
		    			if (left instanceof Lookup && ((Lookup) left).getName().equals(loopVariable)) {
		    				eval = right;
		    			} else if (right instanceof Lookup && ((Lookup) right).getName().equals(loopVariable)) {
		    				eval = left;
		    			} else {
		    				throw new IllegalArgumentException("The loop variable " + loopVariable + " needs to be contained in the for loop end");
		    			}
		    			endComplexity = this.outputComplexity.visit(eval);
		    			break;
	    			default:
	    				throw new IllegalArgumentException(((BinOp)end).getOperator() + " is not a supported binary operator in for loop end");
	    		}
	    		break;
    		default:
    			throw new IllegalArgumentException(end.nodeName() + " is not supported in for loop ends");
    		
    	}
    	
    	MathExpression numIterations = new Division(Arrays.asList(
    			new Subtraction(Arrays.asList(endComplexity, counterComplexity)),
    			changeComplexity));
    	List<AstNode> body = node.getBody();
    	
    	return new Multiplication(Arrays.asList(addAstNodes(body), numIterations));
    }

    @Override
    public MathExpression visitReturn(Return node) {
        return this.visit(node.getValue());
    }

    @Override
    public MathExpression visitIfElse(IfElse node) {
        List<MathExpression> body = new ArrayList<>();
        body.add(addAstNodes(node.getFalseBranch()));
        body.add(addAstNodes(node.getTrueBranch()));
        return new Addition(body);
    }

    @Override
    public MathExpression visitSpecialCall(SpecialCall node) {
        switch(node.getMethodName()) {
            case "length":
                return this.outputComplexity.lookupExpression(node.getObject());
            case "arrayget":
            case "arrayput":
                return addAstNodes(node.getParameters());
            default:
                throw new IllegalArgumentException("what's " + node.getMethodName());
        }
    }

    @Override
    public MathExpression visitCall(Call node) {
        String methodName = node.getMethodName();
        List<MathExpression> runtime = node.getParameters().stream()
                .map(this::visit).collect(Collectors.toList());

        List<MathExpression> output = node.getParameters().stream()
                .map(this.outputComplexity::lookupExpression).collect(Collectors.toList());

        if (this.methodRuntimeComplexities.containsKey(methodName)) {
            runtime.add(this.methodRuntimeComplexities.get(methodName));
            return new Addition(runtime);
        } else if (this.methodSymbolVariables.containsKey(methodName)) {
            Variable symbol = this.methodSymbolVariables.get(methodName);
            runtime.add(new Function(symbol.getName(), output));
        } else {
            throw new IllegalStateException("Unrecognized function " + methodName);
        }
        return new Addition(runtime);
    }

    @Override
    public MathExpression visitBinOp(BinOp node) {
        List<MathExpression> list = new ArrayList<>();
        list.add(visit(node.getLeft()));
        list.add(visit(node.getRight()));
        return new Addition(list);
    }

    @Override
    public MathExpression visitUnaryOp(UnaryOp node) {
        return visit(node.getValue());
    }

    @Override
    public MathExpression visitLiteral(Literal node) {
        return new Constant(4);
    }

    @Override
    public MathExpression visitType(Type node) {
        throw new IllegalStateException("asked for big-O runtime of a type declaration");
    }

    @Override
    public MathExpression visitMultipleAstNodes(MultipleAstNodes node) {
        throw new IllegalStateException("MultipleAstNodes should never be in the final AST");
    }

    private MathExpression addAstNodes(List<AstNode> list) {
        List<MathExpression> sum = new ArrayList<>();
        for (AstNode node : list)
            sum.add(visit(node));
        return new Addition(sum);
    }
}
